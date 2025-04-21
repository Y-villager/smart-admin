package net.lab1024.sa.admin.module.vigorous.sales.outbound.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.enumeration.CommissionTypeEnum;
import net.lab1024.sa.admin.enumeration.SystemYesNo;
import net.lab1024.sa.admin.enumeration.TransferStatusEnum;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity.CommissionRecordEntity;
import net.lab1024.sa.admin.module.vigorous.commission.calc.service.CommissionRecordService;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo.CommissionRuleVO;
import net.lab1024.sa.admin.module.vigorous.commission.rule.service.CommissionRuleCacheService;
import net.lab1024.sa.admin.module.vigorous.commission.rule.service.CommissionRuleService;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.dao.SalesOutboundDao;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.dto.SalesCommissionDto;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.entity.SalesOutboundEntity;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.*;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundExcelVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.BatchUtils;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.ThreadPoolUtils;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static cn.dev33.satoken.SaManager.log;

/**
 * 销售出库 Service
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@Service
public class SalesOutboundService {

    @Resource
    private SalesOutboundDao salesOutboundDao;
    @Autowired
    private SalespersonService salespersonService;
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CommissionRuleService commissionRuleService;
    @Autowired
    private CommissionRecordService commissionRecordService;
    @Autowired
    private BatchUtils batchUtils;

    @Autowired
    private CommissionRuleCacheService commissionRuleCacheService;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SalesOutboundVO> queryPage(SalesOutboundQueryForm queryForm, SalesOutboundExcludeForm excludeForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<SalesOutboundVO> list = salesOutboundDao.queryPage(page, queryForm, excludeForm);
        return SmartPageUtil.convert2PageResult(page, list);
    }


    /**
     * 添加
     */
    public ResponseDTO<String> add(SalesOutboundAddForm addForm) {
        SalesOutboundEntity salesOutboundEntity = SmartBeanUtil.copy(addForm, SalesOutboundEntity.class);
        salesOutboundDao.insert(salesOutboundEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(SalesOutboundUpdateForm updateForm) {
        SalesOutboundEntity salesOutboundEntity = SmartBeanUtil.copy(updateForm, SalesOutboundEntity.class);
        salesOutboundDao.updateById(salesOutboundEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }

        salesOutboundDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long salesBoundId) {
        if (null == salesBoundId) {
            return ResponseDTO.ok();
        }

        salesOutboundDao.deleteById(salesBoundId);
        return ResponseDTO.ok();
    }

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importSalesOutbound(MultipartFile file, Boolean mode) {
        List<SalesOutboundImportForm> dataList;
        List<SalesOutboundImportForm> failedDataList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(SalesOutboundImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        // 批量查询出 出库单、业务员和客户
        // 构建需要查询的数据集合（查询一次，减少查询次数）
        Set<String> billNos = new HashSet<>();
        Set<String> salespersonNames = new HashSet<>();
        Set<String> customerNames = new HashSet<>();

        for (SalesOutboundImportForm form : dataList) {
            billNos.add(form.getBillNo());
            salespersonNames.add(form.getSalespersonName());
            customerNames.add(form.getCustomerName());
        }

        // 批量查询销售出库单据
        List<SalesOutboundEntity> salesOutboundList = salesOutboundDao.queryByBillNos(billNos);
        Map<String, Long> salesOutboundMap = salesOutboundList
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(SalesOutboundEntity::getBillNo, SalesOutboundEntity::getSalesBoundId));

        // 批量查询业务员
        Map<String, Long> salespersonMap = salespersonService.getSalespersonsByNames(salespersonNames);

        // 批量查询客户
        Map<String, Long> customerMap = customerService.queryByCustomerNames(customerNames);

        List<SalesOutboundEntity> entityList = new ArrayList<>();
        for (SalesOutboundImportForm form : dataList) {
            SalesOutboundEntity salesOutboundEntity = convertToEntity(form, salesOutboundMap, salespersonMap, customerMap, failedDataList, mode);
            if (salesOutboundEntity != null) {
                entityList.add(salesOutboundEntity);
            }
        }

        // 批量插入有效数据
        int successTotal = 0;
        if (mode) {  // 追加
            // 批量插入操作
            List<BatchResult> insert = salesOutboundDao.insert(entityList);
            for (BatchResult batchResult : insert) {
                successTotal += batchResult.getParameterObjects().size();
            }
        } else {  // 覆盖
            // 执行批量更新操作
            successTotal = batchUtils.doThreadInsertOrUpdate(entityList, salesOutboundDao, "batchUpdate");
        }

        String failedDataPath = null;
        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            failedDataPath = ExcelUtils.saveFailedDataToExcel(failedDataList, SalesOutboundImportForm.class);
        }

        return ResponseDTO.okMsg("总共" + dataList.size() + "条数据，成功导入" + successTotal + "条，导入失败记录有：" + failedDataList.size() + "条", failedDataPath);

    }

    private SalesOutboundEntity convertToEntity(SalesOutboundImportForm form,
                                                Map<String, Long> salesOutboundMap,
                                                Map<String, Long> salespersonMap,
                                                Map<String, Long> customerMap,
                                                List<SalesOutboundImportForm> failedDataList,
                                                Boolean mode) {
        SalesOutboundEntity entity = new SalesOutboundEntity();

        // 根据 mode 的值简化条件判断，true为追加
        // 检查销售出库单据编号是否重复（批量查询，减少数据库查询次数）
        if (mode) {
            if (salesOutboundMap.containsKey(form.getBillNo())) {
                form.setErrorMsg("追加模式: 系统已存在该单据编号的记录");
                failedDataList.add(form);
                return null;
            }
        } else {
            if (!salesOutboundMap.containsKey(form.getBillNo())) {
                form.setErrorMsg("覆盖模式：系统中不存在该单据编号的记录");
                failedDataList.add(form);
                return null;
            }
        }

        // 根据名称获取业务员id（从缓存中查询）
        Long salespersonId = salespersonMap.get(form.getSalespersonName());
        if (salespersonId == null) {
            form.setErrorMsg("找不到业务员，不允许导入");
            failedDataList.add(form);  // 保存失败的数据
            return null;
        }

        // 客户id（从缓存中查询）
        Long customerId = customerMap.get(form.getCustomerName());
        if (customerId == null) {
            form.setErrorMsg("找不到客户信息，不允许导入");
            failedDataList.add(form);  // 保存失败的数据
            return null;
        }

        // 转换日期格式
        if (form.getSalesBoundDate().contains("-")) {
            LocalDate date = LocalDate.parse(form.getSalesBoundDate());
            entity.setSalesBoundDate(date);
        } else if (form.getSalesBoundDate().contains("/")) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/M/d");
            LocalDate date = LocalDate.parse(form.getSalesBoundDate(), fmt);
            entity.setSalesBoundDate(date);
        }

        // 设置其他字段
        entity.setCustomerId(customerId);
        entity.setSalespersonId(salespersonId);
        entity.setAmount(form.getAmount());
        entity.setBillNo(form.getBillNo());
        entity.setAmount(form.getAmount());

        // 如果所有字段都已正确设置，则返回转换后的实体
        return entity;
    }


    /**
     * 导出
     */
    public List<SalesOutboundExcelVO> getExportList(SalesOutboundQueryForm queryForm, SalesOutboundExcludeForm excludeForm) {
//        List<SalesOutboundEntity> entityList = salesOutboundDao.selectList(null);
        List<SalesOutboundVO> entityList = salesOutboundDao.queryPage(null, queryForm, excludeForm);

        // 使用并行流进行转换，提高处理速度
        return entityList.parallelStream()
                .map(e -> SalesOutboundExcelVO.builder()
                        .customerCode(e.getCustomerCode())
                        .salespersonName(e.getSalespersonName())
                        .billNo(e.getBillNo())
                        .salesBoundDate(e.getSalesBoundDate())
                        .amount(e.getAmount())
                        .customerName(e.getCustomerName())
                        .firstOrderDate(e.getFirstOrderDate())
                        .build()
                ).collect(Collectors.toList());
    }

    /**
     * 生成选中出库单 提成
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> createSelectedCommission(ValidateList<Long> idList) {
        List<SalesCommissionDto> list = salesOutboundDao.queryByIdList(idList);

        ConcurrentLinkedQueue<SalesCommissionDto> errorList = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<CommissionRecordEntity> commissionEntityList = new ConcurrentLinkedQueue<>();
        for (SalesCommissionDto dto : list) {
            classifyCommission(dto, commissionEntityList, errorList);
        }

        //
        return getStringResponseDTO(list, errorList, commissionEntityList);
    }

    private List<SalesCommissionExportForm> convertListToSalesCommission(List<SalesCommissionDto> salesCommissionDtos) {
        // 使用并行流进行转换，提高处理速度
        return salesCommissionDtos.parallelStream()
                .map(e -> SalesCommissionExportForm.builder()
                        .orderDate(e.getOrderDate())
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

    /**
     * 提成分类
     * @param dto
     * @param commissionEntityList
     * @param errorList
     */
    private void classifyCommission(SalesCommissionDto dto, ConcurrentLinkedQueue<CommissionRecordEntity> commissionEntityList, ConcurrentLinkedQueue<SalesCommissionDto> errorList) {
        CommissionRecordEntity business = convertToCommissionEntity(dto, CommissionTypeEnum.BUSINESS);
        CommissionRecordEntity management ;
        if (business.getRemark() != null){
            dto.setErrMsg(business.getRemark());
            errorList.add(dto);
            return;
        }else {
            commissionEntityList.add(business);
        }
        if (dto.getPSalespersonId() != null && dto.getTransferStatus().equals(0)){
            // 有上级id
            management = convertToCommissionEntity(dto, CommissionTypeEnum.MANAGEMENT);
            commissionEntityList.add(management);
        }
    }

    /**
     * 生成业绩提成
     *
     * @param queryForm 查询条件
     * @return
     */
    public ResponseDTO<String> createCommission(SalesOutboundQueryForm queryForm, SalesOutboundExcludeForm excludeForm) {

        // 需要生成业绩提成 的列表
        List<SalesCommissionDto> list = salesOutboundDao.queryPageWithReceivables(null, queryForm, excludeForm);
        if (list.isEmpty()) {
            return ResponseDTO.okMsg("没有需要生成的提成。");
        }

        // 创建自定义线程池
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolUtils.createThreadPool();

        // 使用 CountDownLatch 来同步线程
        CountDownLatch latch = new CountDownLatch(list.size());

        // 数据列表
        ConcurrentLinkedQueue<SalesCommissionDto> errorList = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<CommissionRecordEntity> commissionRecordVOList = new ConcurrentLinkedQueue<>();

        // 提交任务到线程池
        for (SalesCommissionDto dto : list) {
            threadPoolExecutor.submit(() -> {
                try {
                    classifyCommission(dto, commissionRecordVOList, errorList);
                } finally {
                    latch.countDown();  // 完成一个任务后，CountDownLatch 的计数减一
                }
            });
        }

        // 等待所有线程完成
        try {
            latch.await();  // 等待所有线程完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 关闭线程池
        threadPoolExecutor.shutdown();

        // 转换为不可变列表
        return getStringResponseDTO(list, errorList, commissionRecordVOList);
    }

    @NotNull
    private ResponseDTO<String> getStringResponseDTO(List<SalesCommissionDto> list, ConcurrentLinkedQueue<SalesCommissionDto> errorList, ConcurrentLinkedQueue<CommissionRecordEntity> commissionRecordVOList) {
        int inserted = commissionRecordService.batchInsertCommissionRecordAndUpdate(List.copyOf(commissionRecordVOList));

        String message = getCreatedResult(list, errorList, inserted);

        List<SalesCommissionExportForm> salesCommissionDTOs = convertListToSalesCommission(List.copyOf(errorList));

        String failedPath = ExcelUtils.saveFailedDataToExcel(salesCommissionDTOs, SalesCommissionExportForm.class );

        return ResponseDTO.okMsg(message, failedPath);
    }

    private static @NotNull String getCreatedResult(List<?> list, ConcurrentLinkedQueue<?> errorList, int inserted) {
        int totalRecords = list.size();
        int failedRecords = errorList.size();
        return String.format("%d条出库记录。成功生成: %d条。%d条记录生成失败。", totalRecords, inserted, failedRecords);
    }

    /**
     * 设置提成记录基础信息
     * @param entity
     * @param dto
     */
    private void setCommissionBaseInfo(CommissionRecordEntity entity, SalesCommissionDto dto){
        entity.setOrderDate(dto.getOrderDate()); // 1.销售出库日期 / 业务日期
        entity.setSalesBillNo(dto.getSalesBillNo()); // 2.销售出库-单据编号
        entity.setReceiveBillNo(dto.getReceiveBillNo()); // 3.应收表-单据编号
        entity.setCustomerId(dto.getCustomerId());  // 4.必需：客户id

        entity.setSalespersonId(dto.getSalespersonId());  // 5.必需：业务员id
        entity.setSalespersonName(dto.getSalespersonName());  // 5.必需：业务员id
        entity.setCurrentSalespersonLevelName(dto.getSalespersonLevelName()); // 6.当时业务员级别
        entity.setCurrentSalespersonLevelRate(dto.getLevelRate()); // 7.当时业务员级别系数

        entity.setCurrentParentId(dto.getPSalespersonId()); // 9.当时上级id
        entity.setCurrentParentName(dto.getPSalespersonName()); // 9.当时上级id
        entity.setCurrentParentLevelName(dto.getPSalespersonLevelName()); // 10.当时上级级别
        entity.setCurrentParentLevelRate(dto.getPLevelRate()); // 11.当时上级级别系数

        entity.setIsTransfer(dto.getTransferStatus() == 0 ? 0 : 1); // 12.转交

        entity.setIsCustomsDeclaration(dto.getIsCustomsDeclaration()); // 13.是否报关

        entity.setSalesAmount(dto.getSalesAmount()); // 8.销售金额
        entity.setCurrencyType(dto.getCurrencyType()); // 15.币别
        entity.setExchangeRate(dto.getExchangeRate()); // 15. 汇率
        entity.setFallAmount(dto.getFallAmount()); // 15. 税收合计本位币

    }

    /**
     * 检查必须填写值，数据库保存不能为null的值
     * @param salesCommission
     * @return
     */
    private String checkCommission(SalesCommissionDto salesCommission){
        StringBuilder errorMsg = new StringBuilder();

        // 1.是否有业务日期
        if (salesCommission.getOrderDate() != null){
            if (salesCommission.getOrderDate().isBefore(LocalDate.of(2025, 1, 1))){
                // 2025年前销售单
                // 1.1 没有调整首单日期
                LocalDate firstOrderDate = salesCommission.getAdjustedFirstOrderDate();
                if (firstOrderDate == null){
                    errorMsg.append("2025前业务，但未调整业务首单日期；");
                }
            }else {
                // 2025年及以后
                // 1.2 没有首单日期
                if (salesCommission.getFirstOrderDate() == null){
                    errorMsg.append("客户未设置首单日期；");
                }
            }
        }else {
            errorMsg.append("缺少销售出库-业务日期");
        }

        // 2.没有设置转交状态
        if (salesCommission.getTransferStatus() == null ){
            errorMsg.append("客户未设置转交状态；");
        }else {
            Long customerSalesperson = salesCommission.getCustomerSalespersonId();  // 客户相关负责业务员
            if (!customerSalesperson.equals(salesCommission.getSalespersonId())){ // 与负责人id不符
                // 如果客户负责人为上级, 则为转交
                if (salesCommission.getCustomerSalespersonId().equals(salesCommission.getPSalespersonId())){
                    salesCommission.setTransferStatus(SystemYesNo.YES.getValue());
                }else {
                    errorMsg.append("销售出库中业务员与该公司负责业务员不匹配，且非下级，不允许生成提成，请重新查看销售出库。");
                }
            }
        }


        // 4.是否报关
        if (salesCommission.getIsCustomsDeclaration() == null){
            errorMsg.append("客户未设置报关信息");
        }

        // 是否已生成提成
        Integer commissionFlag = salesCommission.getCommissionFlag();
        if (commissionFlag == 1) {
            errorMsg.append("已生成提成记录，请勿重复生成；");
        }
        // 4.是否有应收单-单据编号
        if (salesCommission.getReceiveBillNo() == null) {
            errorMsg.append("缺少应收单-单据编号；");
        }

        // 5.是否有业务员级别
        if (salesCommission.getSalespersonName() == null){
            errorMsg.append("业务员未设置提成级别");
        }

        if (salesCommission.getCustomerSalespersonId() == null){
            errorMsg.append("客户数据中缺少业务员信息；");
        }
        return errorMsg.toString();
    }

    /**
     * 划分数据记录
     * @param salesCommission
     * @param commissionTypeEnum 提成类别
     * @return
     */
    private CommissionRecordEntity convertToCommissionEntity(SalesCommissionDto salesCommission, CommissionTypeEnum commissionTypeEnum) {
        CommissionRecordEntity commission = new CommissionRecordEntity();

        // 检查错误信息
        String errorMsg = checkCommission(salesCommission);
        setCommissionBaseInfo(commission, salesCommission);
        // 有错误信息
        if (!errorMsg.isEmpty()){
            commission.setRemark(errorMsg);
            return commission;
        }
        // 设置 需要动态计算的信息
        commission.setCommissionType(commissionTypeEnum.getValue());
        // 设置
        setCommissionDynamicInfo(commission, salesCommission);
        return commission;
    }

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

    // 计算客户合作年数
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

    public ResponseDTO<String> batchUpdateCommissionFlag(Set<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return ResponseDTO.ok();
        }
        salesOutboundDao.batchUpdateCommissionFlag(idList, 2);
        return ResponseDTO.ok();
    }


}
