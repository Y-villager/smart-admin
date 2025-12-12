package net.lab1024.sa.admin.module.vigorous.commission.calc.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.enumeration.CommissionTypeEnum;
import net.lab1024.sa.admin.enumeration.OrderTypeEnum;
import net.lab1024.sa.admin.enumeration.SystemYesNo;
import net.lab1024.sa.admin.enumeration.TransferStatusEnum;
import net.lab1024.sa.admin.module.vigorous.commission.calc.dao.CommissionRecordDao;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.dto.SalesCommissionDto;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity.CommissionRecordEntity;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form.*;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo.CommissionRecordVO;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo.CommissionRuleVO;
import net.lab1024.sa.admin.module.vigorous.commission.rule.service.CommissionRuleCacheService;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;
import net.lab1024.sa.admin.module.vigorous.res.ValidationResult;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.dao.SalesOutboundDao;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesCommissionExportForm;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.SplitListUtils;
import net.lab1024.sa.admin.util.ThreadPoolUtils;
import net.lab1024.sa.admin.util.ValidationUtils;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.BatchResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.dev33.satoken.SaManager.log;

/**
 * 业务提成记录 Service
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Service
public class CommissionRecordService {

    @Resource
    private CommissionRecordDao commissionRecordDao;
    @Qualifier("salesOutboundDao")
    @Autowired
    private SalesOutboundDao salesOutboundDao;

    // 使用事务模板简化事务的管理
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private SalespersonService salespersonService;
    @Autowired
    private CommissionRuleCacheService commissionRuleCacheService;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CommissionRecordVO> queryPage(CommissionRecordQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<CommissionRecordVO> list = commissionRecordDao.queryPage(page, queryForm);
        PageResult<CommissionRecordVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(CommissionRecordAddForm addForm) {
        CommissionRecordEntity commissionRecordEntity = SmartBeanUtil.copy(addForm, CommissionRecordEntity.class);
        commissionRecordDao.insert(commissionRecordEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(CommissionRecordUpdateForm updateForm) {
        CommissionRecordEntity commissionRecordEntity = SmartBeanUtil.copy(updateForm, CommissionRecordEntity.class);
        commissionRecordDao.updateById(commissionRecordEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        commissionRecordDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long commissionId) {
        if (null == commissionId){
            return ResponseDTO.ok();
        }

        commissionRecordDao.deleteById(commissionId);
        return ResponseDTO.ok();
    }

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importCommissionRecord(MultipartFile file, Boolean mode) {
        List<CommissionRecordImportForm> dataList;
        List<CommissionRecordImportForm> failedDataList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(CommissionRecordImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        // 将 CommissionRecordImportForm 转换为 CommissionRecordEntity，同时记录失败的数据
        List<CommissionRecordEntity> entityList = createImportList(dataList, failedDataList, mode);
        // true 为追加模式，false为按单据编号覆盖
        int successTotal = 0;
        try {
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> insert = commissionRecordDao.insert(entityList);
                for (BatchResult batchResult : insert) {
                    successTotal += batchResult.getParameterObjects().size();
                }
            } else {  // 覆盖
                // 执行批量更新操作
                successTotal = doThreadUpdate(entityList);
            }
        }
        catch (DataAccessException e) {
            // 捕获数据库访问异常
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "数据库操作失败，更新或插入过程中出现异常："+e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常
            return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "发生未知错误："+e.getMessage());
        }

        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            ExcelUtils.saveFailedDataToExcel(failedDataList, CommissionRecordImportForm.class);
        }

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + successTotal + "条，导入失败记录有："+failedDataList.size()+"条" );

    }

    // 生成导入列表
    private List<CommissionRecordEntity> createImportList(List<CommissionRecordImportForm> dataList,
                                                     List<CommissionRecordImportForm> failedDataList,
                                                     boolean mode) {
        
        for (CommissionRecordImportForm form : dataList) {
            }
        // 单据编号 map
    
        // 需要的映射数据
        
        return dataList.parallelStream()
                .map(form -> convertToEntity(form, failedDataList, mode))
                .filter(Objects::nonNull)
                .toList();
    
    }

    // 将 CommissionRecordImportForm 转换为 CommissionRecordEntity
    private CommissionRecordEntity convertToEntity(CommissionRecordImportForm form,
                                                   List<CommissionRecordImportForm> failedDataList,
                                                   boolean mode) {
        CommissionRecordEntity entity = new CommissionRecordEntity();

        return entity;
    }


    /**
     * 公共转换方法
     */
    private Stream<CommissionRecordExportForm> convertAndPrepareExport(CommissionRecordQueryForm queryForm) {
        List<CommissionRecordVO> commissionList = new ArrayList<>();
        if (Boolean.TRUE.equals(queryForm.getIsContainsMaterial()) ){
            commissionList = commissionRecordDao.queryPageWithMaterial(queryForm);
        }else {
            // 获取公共映射和查询结果
            commissionList = commissionRecordDao.queryPage(null, queryForm);
        }
        return commissionList.stream()
                .map(e -> CommissionRecordExportForm.builder()
                        .orderDate(e.getOrderDate())
                        .salesOrderBillNo(e.getSalesOrderBillNo())
                        .salesBillNo(e.getSalesBillNo())
                        .receiveBillNo(e.getReceiveBillNo())

                        .customerName(e.getCustomerName())
                        .customerCode(e.getCustomerCode())
                        .firstOrderDate(e.getFirstOrderDate())
                        .adjustedFirstOrderDate(e.getAdjustedFirstOrderDate())
                        .customerYear(e.getCustomerYear())
                        .customerYearRate(e.getCustomerYearRate())
                        .isCustomsDeclaration(e.getIsCustomsDeclaration() == 0 ? "不报关" : "报关")
                        .isTransfer(e.getIsTransfer() == 0 ? "非转交" : "转交")

                        .salespersonId(e.getSalespersonId())
                        .currentParentId(e.getCurrentParentId())
                        .salespersonName(e.getSalespersonName())
                        .salespersonLevelName(e.getCurrentSalespersonLevelName())
                        .salespersonLevelRate(e.getCurrentSalespersonLevelRate())
                        .currentParentName(e.getCurrentParentName())
                        .currentParentLevelName(e.getCurrentParentLevelName())
                        .currentParentLevelRate(e.getCurrentParentLevelRate())

                        .salesAmount(e.getSalesAmount())
                        .currencyType(e.getCurrencyType())
                        .commissionRate(e.getCommissionRate())
                        .commissionAmount(e.getCommissionAmount())

                        .commissionType(SmartEnumUtil.getEnumDescByValue(e.getCommissionType(), CommissionTypeEnum.class))
                        .exchangeRate(e.getExchangeRate())
                        .fallAmount(e.getFallAmount())

                        .materialItems(e.getMaterialItems())
                        .orderType(e.getOrderType())
                        .build()
                );
    }

    /**
     * 通用分组收集方法
     */
    private Map<String, Collection<?>> groupAndCollect(
            Stream<CommissionRecordExportForm> stream,
            Function<CommissionRecordExportForm, String> classifier) {

        Map<String, List<CommissionRecordExportForm>> grouped = stream
                .collect(Collectors.groupingBy(classifier));

        Map<String, Collection<?>> result = new HashMap<>();
        grouped.forEach((key, list) -> {
            if (!list.isEmpty()) {
                // 计算每个分组的 commissionAmount 总和
                BigDecimal totalAmount = list.stream()
                        .map(CommissionRecordExportForm::getCommissionAmount)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 创建一个新的统计行
                CommissionRecordExportForm totalRecord = CommissionRecordExportForm.builder()
                        .commissionType("总计")
                        .commissionAmount(totalAmount)
                        .build();

                // 将统计行添加到该分组的最后
                list.add(totalRecord);
                result.put(key, list);
            }
        });
        return result;
    }

    // 工具方法：格式化分组键
    private String formatKey(String name, Long id) {
        return name != null ? String.format("%s (%d)", name, id) : String.valueOf(id);
    }

    /**
     * 按提成类型导出
     */
    public Map<String, Collection<?>> exportCommissionRecord(CommissionRecordQueryForm queryForm) {
        if (Boolean.TRUE.equals(queryForm.getIsContainsMaterial())) {
            return exportCommissionRecordWithMaterial(queryForm);
        } else {
            return groupAndCollect(
                    convertAndPrepareExport(queryForm),
                    e -> {
                        String type = e.getCommissionType();
                        if (CommissionTypeEnum.BUSINESS.getDesc().equals(type)) {
                            return CommissionTypeEnum.BUSINESS.getDesc();
                        } else if (CommissionTypeEnum.MANAGEMENT.getDesc().equals(type)) {
                            return CommissionTypeEnum.MANAGEMENT.getDesc();
                        }
                        return "其他、未知";
                    }
            );
        }
    }

    /**
     * 按销售人员导出
     */
    public Map<String, Collection<?>> exportCommissionRecordBySalesperson(CommissionRecordQueryForm queryForm) {
        if (Boolean.TRUE.equals(queryForm.getIsContainsMaterial())) {
            return exportCommissionRecordBySalespersonWithMaterial(queryForm);
        } else {
            return groupAndCollect(
                    convertAndPrepareExport(queryForm),
                    e -> {
                        if (e.getCommissionType().equals(CommissionTypeEnum.BUSINESS.getDesc())) {
                            String name = e.getSalespersonName();
                            Long id = e.getSalespersonId();
                            return formatKey(name, id);
                        } else if (e.getCommissionType().equals(CommissionTypeEnum.MANAGEMENT.getDesc())) {
                            String parentName = e.getCurrentParentName();
                            Long id = e.getCurrentParentId();
                            return formatKey(parentName, id);
                        } else {
                            return "错误数据";
                        }
                    }
            );
        }
    }

    /**
     * 按销售人员导出（包含物料明细）
     */
    private Map<String, Collection<?>> exportCommissionRecordBySalespersonWithMaterial(CommissionRecordQueryForm queryForm) {
        // 获取基础数据
        List<CommissionRecordExportForm> baseRecords = convertAndPrepareExport(queryForm).collect(Collectors.toList());

        // 转换为包含物料明细的完整数据
        List<CommissionRecordExportWithMaterialForm> resultList = convertToMaterialVOList(baseRecords, queryForm.getIsTotal());

        // 按销售人员分组
        return groupAndCollectMaterial(
                resultList,
                e -> {
                    if (e.getCommissionType().equals(CommissionTypeEnum.BUSINESS.getDesc())) {
                        String name = e.getSalespersonName();
                        Long id = e.getSalespersonId();
                        return formatKey(name, id);
                    } else if (e.getCommissionType().equals(CommissionTypeEnum.MANAGEMENT.getDesc())) {
                        String parentName = e.getCurrentParentName();
                        Long id = e.getCurrentParentId();
                        return formatKey(parentName, id);
                    } else {
                        return "错误数据";
                    }
                }
        );
    }

    /**
     * 分组收集方法（用于包含物料明细的数据）
     */
    private Map<String, Collection<?>> groupAndCollectMaterial(List<CommissionRecordExportWithMaterialForm> records,
                                                               Function<CommissionRecordExportWithMaterialForm, String> classifier) {
        return records.stream()
                .collect(Collectors.groupingBy(classifier,
                        Collectors.collectingAndThen(Collectors.toList(), ArrayList::new)));
    }

    /**
     * 将基础数据转换为包含物料明细的VO列表
     */
    private List<CommissionRecordExportWithMaterialForm> convertToMaterialVOList(List<CommissionRecordExportForm> baseRecords, Boolean isTotal) {
        List<CommissionRecordExportWithMaterialForm> resultList = new ArrayList<>();

        for (CommissionRecordExportForm record : baseRecords) {
            // 获取物料明细
            List<ReceivablesDetailsEntity> materialDetails = record.getMaterialItems();

            if (materialDetails == null) {
                // 如果没有物料明细，仍然导出基础数据
                CommissionRecordExportWithMaterialForm vo = convertToMaterialVO(record, null, true);
                resultList.add(vo);
            } else {
                if (OrderTypeEnum.ACCESSORY_SALE.getDisplayName().equals(record.getOrderType()) && isTotal){ // 配件订单
                    ReceivablesDetailsEntity material = new ReceivablesDetailsEntity();
                    material.setMaterialName("配件总数");
                    // 统计总数量
                    int totalQuantity = materialDetails.stream()
                            .mapToInt(ReceivablesDetailsEntity::getSaleQuantity)
                            .sum();

                    material.setSaleQuantity(totalQuantity);

                    CommissionRecordExportWithMaterialForm vo = convertToMaterialVO(record, material, true);

                    resultList.add(vo);
                }
                else { // 整车订单 和 其他
                    for (int i = 0; i < materialDetails.size(); i++) {
                        boolean showBaseData = (i == 0);
                        ReceivablesDetailsEntity material = materialDetails.get(i);
                        CommissionRecordExportWithMaterialForm vo = convertToMaterialVO(record, material, showBaseData);
                        resultList.add(vo);
                    }
                }
            }
        }

        return resultList;
    }

    /**
     * 转换为包含物料明细的VO对象
     */
    private CommissionRecordExportWithMaterialForm convertToMaterialVO(CommissionRecordExportForm record,
                                                                       ReceivablesDetailsEntity material,
                                                                       boolean showBaseData) {
        CommissionRecordExportWithMaterialForm vo = new CommissionRecordExportWithMaterialForm();

        vo.setCommissionType(record.getCommissionType());
        vo.setSalespersonId(record.getSalespersonId());
        vo.setSalespersonName(record.getSalespersonName());

        vo.setCurrentParentId(record.getCurrentParentId());
        vo.setCurrentParentName(record.getCurrentParentName());


        if (showBaseData) {// 设置基础数据
            // 单据
            vo.setSalesOrderBillNo(record.getSalesOrderBillNo());
            vo.setOrderDate(record.getOrderDate());
            vo.setOrderType(record.getOrderType());

            vo.setSalesBillNo(record.getSalesBillNo());
            vo.setReceivablesNo(record.getReceiveBillNo());

            // 业务员
            vo.setSalespersonLevelName(record.getSalespersonLevelName());
            vo.setSalespersonLevelRate(record.getSalespersonLevelRate());
            vo.setCurrentParentLevelName(record.getCurrentParentLevelName());
            vo.setCurrentParentLevelRate(record.getCurrentParentLevelRate());

            // 客户
            vo.setCustomerName(record.getCustomerName());
            vo.setCustomerCode(record.getCustomerCode());
            vo.setIsTransfer(record.getIsTransfer());
            vo.setIsCustomsDeclaration(record.getIsCustomsDeclaration());
            vo.setFirstOrderDate(record.getFirstOrderDate());
            vo.setAdjustedFirstOrderDate(record.getAdjustedFirstOrderDate());
            vo.setCustomerYearRate(record.getCustomerYearRate());

            // 金额
            vo.setCommissionAmount(record.getCommissionAmount());
            vo.setCommissionRate(record.getCommissionRate());
            vo.setFallAmount(record.getFallAmount());
            vo.setSalesAmount(record.getSalesAmount());
            vo.setCurrencyType(record.getCurrencyType());
            vo.setExchangeRate(record.getExchangeRate());
        }

        // 设置物料明细数据
        if (material != null) {
            vo.setMaterialName(material.getMaterialName());
            vo.setSerialNum(material.getSerialNum());
            vo.setSaleUnit(material.getSaleUnit());
            vo.setSalesQuantity(material.getSaleQuantity());
        }

        return vo;
    }


    /**
     * 按提成类型导出（包含物料明细）
     */
    private Map<String, Collection<?>> exportCommissionRecordWithMaterial(CommissionRecordQueryForm queryForm) {
        // 获取基础数据
        List<CommissionRecordExportForm> baseRecords = convertAndPrepareExport(queryForm).toList();

        if (baseRecords.isEmpty()){
            return null;
        }

        // 转换为包含物料明细的完整数据
        List<CommissionRecordExportWithMaterialForm> resultList = convertToMaterialVOList(baseRecords, queryForm.getIsTotal());

        // 按提成类型分组
        return groupAndCollectMaterial(
                resultList,
                e -> {
                    String type = e.getCommissionType();
                    if (CommissionTypeEnum.BUSINESS.getDesc().equals(type)) {
                        return CommissionTypeEnum.BUSINESS.getDesc();
                    } else if (CommissionTypeEnum.MANAGEMENT.getDesc().equals(type)) {
                        return CommissionTypeEnum.MANAGEMENT.getDesc();
                    }
                    return "其他、未知";
                }
        );
    }




    private int doThreadUpdate(List<CommissionRecordEntity> entityList) {
        List<CommissionRecordEntity> updateList = new ArrayList<>();
        // 初始化线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50,
                4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<CommissionRecordEntity>> splitList = SplitListUtils.splitList(entityList, 1000);
        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<CommissionRecordEntity> subList : splitList) {
            threadPool.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    countDownLatch.countDown();
                }
            }));
        }
        try {
            // 让当前线程处于阻塞状态，知道锁存器计数为零
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * 批量插入
     */
    @Transactional
    public int batchInsertCommissionRecordAndUpdate(List<CommissionRecordEntity> commissionRecordVOList) {
        if (commissionRecordVOList == null || commissionRecordVOList.isEmpty()) {
            return 0;
        }

        int batchSize = 1000;
        // 将数据分成多个批次
        List<List<CommissionRecordEntity>> batches = createBatches(commissionRecordVOList, batchSize);

        // 初始化自定义线程池
        ThreadPoolExecutor threadPool = ThreadPoolUtils.createThreadPool();

        List<Future<Integer>> futures = new ArrayList<>();

        // 并行处理每个批次的插入操作
        AtomicInteger count = new AtomicInteger();
        for (List<CommissionRecordEntity> batch : batches) {
            futures.add(threadPool.submit(() -> transactionTemplate.execute(status -> {
                int insert = commissionRecordDao.batchInsertOrUpdate(batch);
                count.addAndGet(insert);
                return insert;
            })));
        }

        // 等待所有线程完成
        for (Future<Integer> future : futures) {
            try {
                future.get();  // 阻塞等待线程完成
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        // 关闭线程池
        threadPool.shutdown();
        return count.get();
    }

    // 将列表分割成多个批次
    private List<List<CommissionRecordEntity>> createBatches(List<CommissionRecordEntity> list, int batchSize) {
        List<List<CommissionRecordEntity>> batches = new ArrayList<>();
        int totalSize = list.size();
        for (int i = 0; i < totalSize; i += batchSize) {
            int end = Math.min(i + batchSize, totalSize);
            batches.add(list.subList(i, end));
        }
        return batches;
    }


    /**
     * 提成分类
     * @param dto
     * @param commissionEntityList
     * @param errorList 错误列表
     */
    public void classifyCommission(SalesCommissionDto dto,
                                    ConcurrentLinkedQueue<CommissionRecordEntity> commissionEntityList,
                                    ConcurrentLinkedQueue<CommissionRecordEntity> managementEntityList,
                                    ConcurrentLinkedQueue<SalesCommissionDto> errorList) {
        //
        CommissionRecordEntity business = convertToCommissionEntity(dto, CommissionTypeEnum.BUSINESS);
        CommissionRecordEntity management ;

        ValidationResult remark = business.getRemark();
        if (remark != null){  // 如果备注不为空
            if (remark.hasErrors()){
                dto.setErrMsg(remark.getErrorMsg());
                errorList.add(dto);
                return;
            }
            if (remark.hasReminds()){
                dto.setRemindMsg(remark.getRemindMsg());
//                errorList.add(dto);
            }
        }
        // 没有错误信息，就加入插入列表中
        commissionEntityList.add(business);
        // 如果存在上级，且转交状态为自主开发，则生成上级的管理提成
        if (dto.getPSalespersonId() != null && TransferStatusEnum.INDEPENDENTLY.getValue().equals(dto.getTransferStatus())){
            // 有上级id
            management = convertToCommissionEntity(dto, CommissionTypeEnum.MANAGEMENT);
            managementEntityList.add(management);
        }
    }

    /**
     * 划分数据记录
     * @param salesCommission
     * @param commissionTypeEnum 提成类别
     * @return
     */
    private CommissionRecordEntity convertToCommissionEntity(SalesCommissionDto salesCommission, CommissionTypeEnum commissionTypeEnum) {
        CommissionRecordEntity commission = new CommissionRecordEntity();

        // 设置基本信息 检查错误信息
        ValidationResult result = checkCommissionAndSetEntity(commission, salesCommission);

        if (result.hasErrors()){ // 如果有错误信息
            commission.setRemark(result);
            return commission;
        }
        if (result.hasReminds()){
            commission.setRemark(result);
        }

        // 设置 需要动态计算的信息
        commission.setCommissionType(commissionTypeEnum.getValue());
        // 设置
        setCommissionDynamicInfo(commission, salesCommission);
        return commission;
    }

    /**
     * 检查必须填写值，数据库保存不能为null的值
     * @return
     */
    private ValidationResult checkCommissionAndSetEntity(CommissionRecordEntity entity, SalesCommissionDto dto) {
        ValidationResult result = new ValidationResult();

        // 1. 出库单信息
        ValidationUtils.checkOutboundDate(dto, result);
        entity.setOutboundDate(dto.getOutboundDate()); // 1.销售出库日期 / 业务日期

        if (StringUtils.isBlank(dto.getSalesBillNo())) {
            result.addError("销售出库-单据编号不能为空");
        }
        entity.setSalesBillNo(dto.getSalesBillNo()); // 2.销售出库-单据编号

        // 2. 转交状态检查
        ValidationUtils.checkTransferStatus(dto, result);
        if (dto.getTransferStatus() != null){
            entity.setIsTransfer(dto.getTransferStatus() == 0 ? 0 : 1); // 12.转交
        }

        // 3. 报关信息检查
        ValidationUtils.checkCustomsDeclaration(dto, result);
        entity.setIsCustomsDeclaration(dto.getIsCustomsDeclaration()); // 13.是否报关

        // 4. 提成记录检查
        ValidationUtils.checkCommissionFlag(dto, result);

        // 5. 应收单编号检查
        if (StringUtils.isBlank(dto.getReceiveBillNo())) {
            result.addError("应收单-单据编号不能为空");
            return result;
        }
        entity.setReceiveBillNo(dto.getReceiveBillNo()); // 3.应收表-单据编号

        // 6. 业务员级别检查
        if (StringUtils.isBlank(dto.getSalespersonName())) {
            result.addError("业务员名称不能为空");
        }
        entity.setSalespersonId(dto.getSalespersonId());  // 5.必需：业务员id
        entity.setSalespersonName(dto.getSalespersonName());  // 5.必需：业务员名称
        entity.setCurrentSalespersonLevelRate(dto.getLevelRate()); // 7.当时业务员级别系数
        entity.setCurrentSalespersonLevelName(dto.getSalespersonLevelName()); // 6.当时业务员级别

        entity.setCurrentParentId(dto.getPSalespersonId()); // 9.当时上级id
        entity.setCurrentParentName(dto.getPSalespersonName()); // 9.当时上级id
        entity.setCurrentParentLevelName(dto.getPSalespersonLevelName()); // 10.当时上级级别
        entity.setCurrentParentLevelRate(dto.getPLevelRate()); // 11.当时上级级别系数

        // 7. 客户业务员信息检查
        ValidationUtils.checkCustomerSalesperson(dto, result);

        // 9. 销售订单信息
        if (StringUtils.isBlank(dto.getSalesOrderBillNo())) {
            result.addError("销售订单-单据编号不能为空");
        }
        entity.setOrderDate(dto.getOrderDate());

        entity.setSalesOrderBillNo(dto.getSalesOrderBillNo());

        // 10. 发货通知单
        if (StringUtils.isBlank(dto.getFSalesBillNo())) {
            result.addError("发货通知单-单据编号不能为空");
        }
        entity.setFSalesBillNo(dto.getFSalesBillNo());

        entity.setCustomerId(dto.getCustomerId());  // 4.必需：客户id

        // 11. 检查金额
        ValidationResult validateAmount = ValidationUtils.validateAmount(dto.getSalesAmount(), dto.getExchangeRate(), dto.getFallAmount(), 2);
        if (validateAmount.hasErrors()) {
            result.addError(validateAmount.getErrorMsg());
        }

        entity.setSalesAmount(dto.getSalesAmount()); // 8.销售金额
        entity.setCurrencyType(dto.getCurrencyType()); // 15.币别
        entity.setExchangeRate(dto.getExchangeRate()); // 15. 汇率
        entity.setFallAmount(dto.getFallAmount()); // 15. 税收合计本位币

        return result;
    }


    /**
     * 设置提成动态信息
     * @param entity
     * @param salesCommission
     */
    private void setCommissionDynamicInfo(CommissionRecordEntity entity, SalesCommissionDto salesCommission) {
        BigDecimal hundred = BigDecimal.valueOf(100);

        // 查询提成规则
        CommissionRuleVO commissionRule =
                commissionRuleCacheService.getCommissionRuleFromCache(entity.getCommissionType(), salesCommission.getTransferStatus(), salesCommission.getIsCustomsDeclaration());

        // 1.计算客户合作年数
        Integer customerYear = calculateCooperationYears(salesCommission, LocalDate.now());
        entity.setCustomerYear(customerYear); // 客户合作年数

        // 2.计算客户合作年份系数
        BigDecimal customerYearRate = calcCustomerYearRate(customerYear); // 客户年份系数
        entity.setCustomerYearRate(customerYearRate);

        // 计算百分比时使用 hundred 的倒数
        BigDecimal hundredInverse = BigDecimal.ONE.divide(hundred, 4, RoundingMode.HALF_UP);

        BigDecimal amount = Optional.ofNullable(salesCommission.getFallAmount())
                .orElse(BigDecimal.ZERO);   // 税收合计本位币
        BigDecimal levelRate = salesCommission.getLevelRate();  // 业务员提成级别系数
        BigDecimal pLevelRate = salesCommission.getPLevelRate();    // 上级提成级别

        BigDecimal commissionRate;  // 最总提成系数
        BigDecimal commissionAmount;      // 提成金额
        // 3.业务提成
        if (entity.getCommissionType().equals(CommissionTypeEnum.BUSINESS.getValue())) {
            // 是否是动态计算
            if (commissionRule.getUseDynamicFormula() != null && commissionRule.getUseDynamicFormula().equals(1)) {
                // 动态计算 系数=当前业务级别 * 客户年份系数
                BigDecimal resRate = levelRate.multiply(customerYearRate);
                commissionRate = resRate;
            } else { // 规则表 固定系数
                commissionRate = commissionRule.getCommissionRate();
            }
            commissionAmount = amount.multiply(commissionRate).multiply(hundredInverse);
            entity.setCommissionRate(commissionRate);
            entity.setCommissionAmount(commissionAmount);
        } else if (entity.getCommissionType().equals(CommissionTypeEnum.MANAGEMENT.getValue())) {
            if (commissionRule.getUseDynamicFormula().equals(1)) {
                // 动态计算：（上级系数-自身系数）* 客户年份系数
                if (salesCommission.getPLevelRate() != null) {
                    // 如果存在上级id，计算 管理提成
                    if (pLevelRate.compareTo(levelRate) > 0) { // 上级系数不能比自身小
                        // 管理提成比例：客户年份系数 * （上级 - 自身系数）
                        commissionRate = customerYearRate.multiply(pLevelRate.subtract(levelRate));
                    } else {
                        throw new RuntimeException("上级级别不能小于业务员");
                    }
                }else {
                    throw new RuntimeException("计算管理提成，但缺少上级id");
                }
            } else {
                // 固定系数 * 客户系数
                commissionRate = customerYearRate.multiply(commissionRule.getCommissionRate());
            }

            commissionAmount = amount.multiply(commissionRate).multiply(hundredInverse);
            entity.setCommissionRate(commissionRate);
            entity.setCommissionAmount(commissionAmount);
        }else {
            throw new RuntimeException("系统提成计算出错，缺少提成类别信息，请查看代码");
        }

    }

    /**
     * 计算客户合作年数
     * @param dto
     * @param today
     * @return
     */
    public static int calculateCooperationYears(SalesCommissionDto dto, LocalDate today) {
        if (dto.getFirstOrderDate() == null){
            throw new RuntimeException("缺少客户首单日期");
        }
        LocalDate startDate = dto.getFirstOrderDate();
        if (dto.getAdjustedFirstOrderDate() != null){
            startDate = dto.getAdjustedFirstOrderDate();
        }
        // 计算开始日期与今天的日期差距
        Period period = Period.between(startDate, today);

        // 通过年数判断合作年数
        if (period.getYears() > 0) {
            return period.getYears() + 1; // 已经完成某年数，并加1
        } else if (period.getMonths() == 0 && period.getDays() == 0) {
            // 如果正好一年，返回合作的第1年
            return 1;
        } else {
            return 0; // 不满1年的合作
        }
    }

    /**
     * 计算客户年份系数
     *
     * @param year
     * @return
     */
    public static BigDecimal calcCustomerYearRate(Integer year) {
        if (year == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(1.0 - Math.max(0, year - 1) * 0.1);
    }


    /**
     * 创建提成记录
     * @param list
     * @param commissionRecordVOList
     * @param managementRecordVOList
     * @param errorList
     * @return
     */
    @Transactional
    @NotNull
    public ResponseDTO<String> insertAndGetStringResponseDTO(List<SalesCommissionDto> list,
                                                             ConcurrentLinkedQueue<CommissionRecordEntity> commissionRecordVOList,
                                                             ConcurrentLinkedQueue<CommissionRecordEntity> managementRecordVOList,
                                                             ConcurrentLinkedQueue<SalesCommissionDto> errorList) {
        int inserted = batchInsertCommissionRecordAndUpdate(List.copyOf(commissionRecordVOList));
        int insertedOfManage = batchInsertCommissionRecordAndUpdate(List.copyOf(managementRecordVOList));

        String message = getCreatedResult(list, inserted, insertedOfManage, errorList);

        List<SalesCommissionExportForm> salesCommissionDTOs = convertListToSalesCommission(List.copyOf(errorList));

        String failedPath = ExcelUtils.saveFailedDataToExcel(salesCommissionDTOs, SalesCommissionExportForm.class );

        return ResponseDTO.okMsg(message, failedPath);
    }

    /**
     * 格式化输出
     * @param list
     * @param inserted
     * @param insertedOfManage
     * @param errorList
     * @return
     */
    private static @NotNull String getCreatedResult(List<?> list,
                                                    int inserted,
                                                    int insertedOfManage,
                                                    ConcurrentLinkedQueue<?> errorList) {
        int totalRecords = list.size();
        int failedRecords = errorList.size();
        return String.format("%d条记录。成功生成: %d条。%d条记录生成失败。生成%d条管理提成。", totalRecords, inserted, failedRecords, insertedOfManage);
    }


    public ResponseDTO<String> batchUpdateCommissionFlag(Set<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        salesOutboundDao.batchUpdateCommissionFlag(idList, 2);
        return ResponseDTO.ok();
    }

    /**
     * 转换Dto成销售提成
     * @param salesCommissionDtos
     * @return
     */
    private List<SalesCommissionExportForm> convertListToSalesCommission(List<SalesCommissionDto> salesCommissionDtos) {
        // 使用并行流进行转换，提高处理速度
        return salesCommissionDtos.parallelStream()
                .map(e -> SalesCommissionExportForm.builder()
                        .orderDate(e.getOrderDate())
                        .salesOrderBillNo(e.getSalesOrderBillNo())
                        .fSalesBillNo(e.getFSalesBillNo())
                        .salesBillNo(e.getSalesBillNo())
                        .receiveBillNo(e.getReceiveBillNo())
                        .salesAmount(e.getSalesAmount())
                        .currencyType(e.getCurrencyType())
                        .customerCode(e.getCustomerCode())
                        .customerName(e.getCustomerName())
                        .levelRate(e.getLevelRate())
                        .firstOrderDate(e.getFirstOrderDate())
                        .adjustedFirstOrderDate(e.getAdjustedFirstOrderDate())
                        .salespersonName(e.getSalespersonName())
                        .currentParentName(e.getPSalespersonName())
                        .transferStatus(SmartEnumUtil.getEnumDescByValue(e.getTransferStatus(), TransferStatusEnum.class))
                        .isDeclared(SmartEnumUtil.getEnumDescByValue(e.getIsCustomsDeclaration(), SystemYesNo.class))
                        .errMsg(e.getErrMsg())
                        .build()
                ).collect(Collectors.toList());
    }



}
