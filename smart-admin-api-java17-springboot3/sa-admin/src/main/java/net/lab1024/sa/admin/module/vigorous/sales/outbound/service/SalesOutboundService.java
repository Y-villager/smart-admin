package net.lab1024.sa.admin.module.vigorous.sales.outbound.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.dto.SalesCommissionDto;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity.CommissionRecordEntity;
import net.lab1024.sa.admin.module.vigorous.commission.calc.service.CommissionRecordService;
import net.lab1024.sa.admin.module.vigorous.commission.rule.service.CommissionRuleCacheService;
import net.lab1024.sa.admin.module.vigorous.customer.domain.entity.CustomerEntity;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.fsales.service.FSalesService;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.dao.SalesOutboundDao;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.entity.FOutboundRelEntity;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.entity.SalesOutboundEntity;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.*;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundExcelVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.BatchUtils;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.ThreadPoolUtils;
import net.lab1024.sa.base.common.code.UserErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.domain.ValidateList;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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
    private CommissionRecordService commissionRecordService;
    @Autowired
    private FSalesService fSalesService;

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

        // 1. 读取excel数据
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

        // 2. 整理数据（按出库单号分组合并）
        Map<String, List<SalesOutboundImportForm>> groupedData = groupDataByBillNo(dataList);

        // 发货出库关系 出库单-源单（发货单）
        List<FOutboundRelEntity> outboundRelations = new ArrayList<>();

        // 3. 批量查询相关数据（减少数据库查询）
        // 批量查询出 出库单、业务员和客户
        // 构建需要查询的数据集合（查询一次，减少查询次数）
        Set<String> billNos = new HashSet<>();
        Set<String> salespersonNames = new HashSet<>();
        Set<String> customerNames = new HashSet<>();
        Set<String> originBillNos = new HashSet<>();

        // 收集所有需要查询的数据
        for (List<SalesOutboundImportForm> forms : groupedData.values()) {
            if (!forms.isEmpty()) {
                salespersonNames.add(forms.get(0).getSalespersonName());
                customerNames.add(forms.get(0).getCustomerName());
                billNos.add(forms.get(0).getBillNo());
                for (SalesOutboundImportForm form : forms) {
                    originBillNos.add(form.getOriginBillNo());
                }
            }
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

        Set<String> existingOriginBillNos = fSalesService.getExistingBillNo(originBillNos);

        // 4. 转换实体
        List<SalesOutboundEntity> entityList = new ArrayList<>();
        for (List<SalesOutboundImportForm> forms : groupedData.values()) {
            SalesOutboundImportForm importForm = forms.get(0);

            // 收集所有源单号
            for (SalesOutboundImportForm form : forms) {
                // 源单是否存在
                if (existingOriginBillNos.contains(form.getOriginBillNo())) {
                    FOutboundRelEntity relation = new FOutboundRelEntity();
                    relation.setFSalesNo(form.getOriginBillNo());
                    relation.setOutboundNo(importForm.getBillNo());
                    outboundRelations.add(relation);
                }else {
                    form.setErrorMsg("缺少发货单记录");
                    failedDataList.add(importForm);
                }
            }

            SalesOutboundEntity salesOutboundEntity = convertToEntity(importForm, salesOutboundMap, salespersonMap, customerMap, mode);

            if (salesOutboundEntity != null ) {
                entityList.add(salesOutboundEntity);
            }else {
                failedDataList.add(importForm);
            }
        }

        // 5. 批量插入有效数据
        int successTotal = saveAllData(entityList, outboundRelations, mode);

        String failedDataPath = null;
        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            failedDataPath = ExcelUtils.saveFailedDataToExcel(failedDataList, SalesOutboundImportForm.class);
        }

        return ResponseDTO.okMsg(
                String.format("总共 %d 条数据，成功导入 %d 条，失败 %d 条",
                        groupedData.size(),
                        successTotal,
                        failedDataList.size()
                ),
                failedDataPath
        );
    }

    /**
     * 保存所有数据
     */
    private int saveAllData(List<SalesOutboundEntity> entityList,
                            List<FOutboundRelEntity> relations,
                            boolean mode) {

        int successTotal = 0;

        try {
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> insert = salesOutboundDao.insert(entityList);
                for (BatchResult batchResult : insert) {
                    successTotal += batchResult.getParameterObjects().size();
                }
            } else {  // 覆盖
                // 执行批量更新操作
                successTotal = batchUtils.doThreadInsertOrUpdate(entityList, salesOutboundDao, "batchUpdate", true);
            }
            // 保存出库单-发货单多对多关系
            saveOutboundRelations(relations, mode);

        } catch (Exception e) {
            log.error("保存数据失败", e);
            throw new BusinessException("保存数据失败: " + e.getMessage());
        }

        return successTotal;
    }


    /**
     * 保存出库单-发货单多对多关系
     */
    private void saveOutboundRelations(List<FOutboundRelEntity> relations, boolean mode) {

        if (relations.isEmpty() ) {
            return;
        }

        try {
            // 2. 如果是覆盖模式，先删除已存在的关联关系
            if (!mode) {  // 覆盖模式，mode=false表示覆盖
                // 收集需要删除关联关系的出库单号
                int i = salesOutboundDao.deleteExistingRelations(relations);
                System.out.println(i);
            }

            // 3. 批量插入新的关联关系
            salesOutboundDao.batchInsertRelations(relations);

        } catch (Exception e) {
            log.error("保存出库单-发货单关系失败", e);
            throw new BusinessException("保存关联关系失败: " + e.getMessage());
        }

    }

    /**
     * 按单据编号分组数据
     * @param dataList
     * @return
     */
    private Map<String, List<SalesOutboundImportForm>> groupDataByBillNo(List<SalesOutboundImportForm> dataList) {
        Map<String, List<SalesOutboundImportForm>> groupedData = new LinkedHashMap<>();

        for (SalesOutboundImportForm form : dataList) {
            if (StringUtils.isBlank(form.getBillNo())) {
                throw new BusinessException("存在单据编号为空的数据行");
            }

            groupedData.computeIfAbsent(form.getBillNo(), k -> new ArrayList<>()).add(form);
        }

        return groupedData;
    }



    private SalesOutboundEntity convertToEntity(SalesOutboundImportForm form,
                                                Map<String, Long> salesOutboundMap,
                                                Map<String, Long> salespersonMap,
                                                Map<String, Long> customerMap,
                                                Boolean mode) {
        SalesOutboundEntity entity = new SalesOutboundEntity();

        // 根据 mode 的值简化条件判断，true为追加
        // 检查销售出库单据编号是否重复（批量查询，减少数据库查询次数）
        if (mode) {
            if (salesOutboundMap.containsKey(form.getBillNo())) {
                form.setErrorMsg("追加模式: 系统已存在该单据编号的记录");
                return null;
            }
        } else {
            if (!salesOutboundMap.containsKey(form.getBillNo())) {
                form.setErrorMsg("覆盖模式：系统中不存在该单据编号的记录");
                return null;
            }
        }

        // 根据名称获取业务员id（从缓存中查询）
        Long salespersonId = salespersonMap.get(form.getSalespersonName());
        if (salespersonId == null) {
            form.setErrorMsg("找不到业务员，不允许导入");
            return null;
        }

        // 客户id（从缓存中查询）
        Long customerId = customerMap.get(form.getCustomerName());
        if (customerId == null) {
            form.setErrorMsg("找不到客户信息，不允许导入");
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
//        entity.setOriginBillNo(form.getOriginBillNo());

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
        ConcurrentLinkedQueue<CommissionRecordEntity> managementEntityList = new ConcurrentLinkedQueue<>();

        // 分类提成
        for (SalesCommissionDto dto : list) {
            commissionRecordService.classifyCommission(dto, commissionEntityList,managementEntityList, errorList);
        }
        //
        return commissionRecordService.insertAndGetStringResponseDTO(list, commissionEntityList, managementEntityList, errorList);
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
        ConcurrentLinkedQueue<CommissionRecordEntity> managementRecordVOList = new ConcurrentLinkedQueue<>();

        // 提交任务到线程池
        for (SalesCommissionDto dto : list) {
            threadPoolExecutor.submit(() -> {
                try {
                    // 提成分类
                    commissionRecordService.classifyCommission(dto, commissionRecordVOList, managementRecordVOList, errorList);
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
        return commissionRecordService.insertAndGetStringResponseDTO(list, commissionRecordVOList, managementRecordVOList, errorList );
    }


    public ResponseDTO<String> initCustomerFirstOrderDate(ValidateList<Long> customerIds) {
        // 查询存在首单的客户
        List<CustomerEntity> existingFirstOrderCustomers = customerService.getCustomersWithFirstOrder(customerIds);

        Set<Long> existingCustomerIds = existingFirstOrderCustomers.stream()
                .map(CustomerEntity::getCustomerId)
                .collect(Collectors.toSet());

        List<Long> allowedCustomerIds = customerIds.stream()
                .filter(customerId -> !existingCustomerIds.contains(customerId))
                .toList();

        if (allowedCustomerIds.isEmpty()) {
            return ResponseDTO.error(UserErrorCode.ALREADY_EXIST,"所有选中的客户均已存在首单数据，无法重复生成");
        }

        // 1. 查询有首单记录的客户
        Set<CustomerEntity> customerEntities = salesOutboundDao.queryFirstOrdersByCustomerId(allowedCustomerIds);

        // 2. 提取已找到首单的客户ID
        Set<Long> foundCustomerIds = customerEntities.stream()
                .map(CustomerEntity::getCustomerId)
                .collect(Collectors.toSet());

        // 3. 找出没有首单记录的客户ID
        List<Long> notFoundCustomerIds = customerIds.stream()
                .filter(customerId -> !foundCustomerIds.contains(customerId))
                .toList();
        // 4. 构建返回信息
        StringBuilder message = new StringBuilder();

        int i = customerService.batchUpdate(customerEntities);

        message.append("成功为 ").append(i).append(" 个客户生成首单数据");

        return ResponseDTO.ok(message.toString());
    }
}
