package net.lab1024.sa.admin.module.vigorous.sales.outbound.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.enumeration.CommissionTypeEnum;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo.CommissionRecordVO;
import net.lab1024.sa.admin.module.vigorous.commission.calc.service.CommissionRecordService;
import net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo.CommissionRuleVO;
import net.lab1024.sa.admin.module.vigorous.commission.rule.service.CommissionRuleService;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.dao.SalesOutboundDao;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.dto.SalesCommissionDto;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.entity.SalesOutboundEntity;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundAddForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundImportForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundQueryForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundUpdateForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundExcelVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
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





    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SalesOutboundVO> queryPage(SalesOutboundQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        List<SalesOutboundVO> list = salesOutboundDao.queryPage(page, queryForm);

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
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        salesOutboundDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long salesBoundId) {
        if (null == salesBoundId){
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
    public ResponseDTO<String> importSalesOutbound(MultipartFile file) {
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

        List<SalesOutboundEntity> entityList = dataList.parallelStream()
                .map(form -> convertToEntity(form, salesOutboundMap, salespersonMap, customerMap, failedDataList))
                .filter(Objects::nonNull)
                .toList();

        // 批量插入有效数据
        List<BatchResult> insert = salesOutboundDao.insert(entityList);

        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            String file1 = ExcelUtils.saveFailedDataToExcel(failedDataList, SalesOutboundImportForm.class);
        }

        if (insert.isEmpty()){
            return ResponseDTO.okMsg("全部导入失败");
        }
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + insert.get(0).getParameterObjects().size() + "条，导入失败记录有："+failedDataList.size()+"条" );

    }
    private SalesOutboundEntity convertToEntity(SalesOutboundImportForm form,
                                                Map<String, Long> salesOutboundMap,
                                                Map<String, Long> salespersonMap,
                                                Map<String, Long> customerMap,
                                                List<SalesOutboundImportForm> failedDataList) {
        SalesOutboundEntity entity = new SalesOutboundEntity();

        // 检查销售出库单据编号是否重复（批量查询，减少数据库查询次数）
        if (salesOutboundMap.containsKey(form.getBillNo())) {
            form.setErrorMsg("单据编号重复，不允许导入");
            failedDataList.add(form);  // 保存失败的数据
            return null;  // 只返回空的实体，标记失败
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

        // 如果所有字段都已正确设置，则返回转换后的实体
        return entity;
    }



    /**
     * 导出
     */
    public List<SalesOutboundExcelVO> getExportList(SalesOutboundQueryForm queryForm) {
//        List<SalesOutboundEntity> entityList = salesOutboundDao.selectList(null);
        List<SalesOutboundVO> entityList = salesOutboundDao.queryPage(null,queryForm);

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
     * 生成业绩提成
     * @param queryForm 查询条件
     * @return
     */
    public ResponseDTO<String> createCommission(SalesOutboundQueryForm queryForm) {

        // 需要生成业绩提成 的列表
        List<SalesCommissionDto> list = salesOutboundDao.queryPageWithReceivables(null, queryForm);

        // 缓存常用的计算值，避免在每次遍历时重复计算
        BigDecimal hundred = BigDecimal.valueOf(100);

        // 提前缓存业务和管理规则
        final int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors();  // CPU 核心数
        final int MAX_POOL_SIZE = 2 * CORE_POOL_SIZE;  // 最大线程数
        final long KEEP_ALIVE_TIME = 60L;  // 线程空闲超时时间（秒）
        // 创建自定义线程池
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),  // 阻塞队列，最大排队任务数
                new ThreadPoolExecutor.CallerRunsPolicy()  // 当线程池饱和时，直接在主线程中执行任务
        );

        ConcurrentLinkedQueue<CommissionRecordVO> commissionRecordVOList = new ConcurrentLinkedQueue<>();

        // 使用 CountDownLatch 来同步线程
        CountDownLatch latch = new CountDownLatch(list.size());

        AtomicInteger noFirstCount = new AtomicInteger();
        AtomicInteger isGenerated = new AtomicInteger();
        // 提交任务到线程池
        for (SalesCommissionDto salesCommission : list) {
            threadPoolExecutor.submit(() -> {
                try {
                    // 查询缓存
                    CommissionRuleVO business = commissionRuleService.queryCommissionRuleFromCache(salesCommission, CommissionTypeEnum.BUSINESS);
                    CommissionRuleVO management = commissionRuleService.queryCommissionRuleFromCache(salesCommission, CommissionTypeEnum.MANAGEMENT);

                    // 初始化 CommissionRecordVO
                    CommissionRecordVO recordVO = initCommissionRecordVO(salesCommission, business, management, hundred);

                    // 分类存储
                    if (recordVO.getFirstOrderDate() == null) {
                        noFirstCount.getAndIncrement();
                    } else if (recordVO.getCommissionFlag() == 1) {
                        isGenerated.getAndIncrement();
                    } else {
                        commissionRecordVOList.add(recordVO); // 记录需要处理的列表
                    }
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
        List<CommissionRecordVO> finalCommissionList = List.copyOf(commissionRecordVOList);

        int insert = commissionRecordService.batchInsertCommissionRecordAndUpdate(finalCommissionList);
        StringBuilder res = new StringBuilder();
        if (insert > 0) {
            // 生成提成记录的成功信息
            res.append("成功生成: ").append(insert).append("条提成记录。");
        }

        // 判断并追加没有首单日期的记录信息
        if (noFirstCount.get() > 0) {
            res.append("生成失败：").append(noFirstCount.get()).append("条没有首单日期。");
        }

        // 判断并追加已创建记录的数量
        if (isGenerated.get() > 0) {
            res.append("生成失败：").append(isGenerated.get()).append("条记录已创建。");
        }

        return ResponseDTO.okMsg(res.toString());
    }

    private @NotNull CommissionRecordVO initCommissionRecordVO(SalesCommissionDto salesOutbound,
                                                               CommissionRuleVO business,
                                                               CommissionRuleVO management,
                                                               BigDecimal hundred) {
        CommissionRecordVO recordVO = new CommissionRecordVO();

        // 设置基本信息
        recordVO.setSalesOutboundId(salesOutbound.getSalesOutboundId());   // 销售出库id
        recordVO.setSalesBillNo(salesOutbound.getSalesBillNo());   // 销售出库id
        recordVO.setSalespersonId(salesOutbound.getSalespersonId());    // 业务员id
        recordVO.setCustomerId(salesOutbound.getCustomerId());  // 客户id
        recordVO.setSalesAmount(salesOutbound.getSalesAmount()); // 销售金额
        recordVO.setOrderDate(salesOutbound.getSalesBoundDate()); // 销售出库日期 / 业务日期
        recordVO.setSalesBillNo(salesOutbound.getSalesBillNo()); // 销售-单据编号
        recordVO.setFirstOrderDate(salesOutbound.getFirstOrderDate()); // 首单日期
        recordVO.setAdjustedFirstOrderDate(salesOutbound.getAdjustedFirstOrderDate()); // 调整后-首单日期
        recordVO.setCommissionFlag(salesOutbound.getCommissionFlag()); // 销售-提成标识

        // 客户年份
        Integer customerYear = null;

        // 没有首单日期
        if (salesOutbound.getFirstOrderDate() == null || salesOutbound.getCommissionFlag() == 1) {
            return recordVO;  // 如果 FirstOrderDate 为 null，直接返回 recordVO
        }

        LocalDate firstOrderDate = salesOutbound.getAdjustedFirstOrderDate() != null
                ? salesOutbound.getAdjustedFirstOrderDate()
                : salesOutbound.getFirstOrderDate();

        customerYear = calculateCooperationYears(firstOrderDate, LocalDate.now());


        // 计算百分比时使用 hundred 的倒数
        BigDecimal hundredInverse = BigDecimal.ONE.divide(hundred, 4, RoundingMode.HALF_UP);

        //
        BigDecimal amount = Optional.ofNullable(salesOutbound.getSalesAmount()).orElse(BigDecimal.ZERO);
        BigDecimal levelRate = Optional.ofNullable(salesOutbound.getLevelRate()).orElse(BigDecimal.ZERO);
        BigDecimal pLevelRate = Optional.ofNullable(salesOutbound.getPLevelRate()).orElse(BigDecimal.ZERO);

        // 设置提成相关数据
        BigDecimal customerYearRate = calcCustomerYearRate(customerYear); // 客户年份系数
        recordVO.setCustomerYear(customerYear); // 客户合作年数
        recordVO.setCustomerYearRate(customerYearRate);

        BigDecimal businessCommissionRate = BigDecimal.ZERO;
        BigDecimal businessCommission = BigDecimal.ZERO;
        // 业务提成
        if (business != null){
            // 是否是动态计算
            if (business.getUseDynamicFormula() != null && business.getUseDynamicFormula().equals(1)){ // 动态计算 当前业务级别 * 客户年份系数
                BigDecimal resRate = levelRate.multiply(customerYearRate);
                businessCommissionRate = resRate;
                businessCommission = amount.multiply(resRate).multiply(hundredInverse);
            }else { // 规则表 固定系数
                businessCommissionRate = Optional.ofNullable(business.getCommissionRate()).orElse(BigDecimal.ZERO);
                businessCommission = businessCommissionRate.multiply(amount).multiply(hundredInverse);
            }
            recordVO.setBusinessCommissionAmount(businessCommission);
            recordVO.setBusinessCommissionRate(businessCommissionRate);
        }

        BigDecimal managementCommissionRate = BigDecimal.ZERO;
        BigDecimal managementCommission = BigDecimal.ZERO;
        // 管理提成
        if (management != null){
            if (management.getUseDynamicFormula() != null) { // 动态计算：（上级系数-自身系数）* 客户年份系数
                // 如果存在上级id，计算 管理提成
                if (salesOutbound.getPLevelRate() != null){
                    if (pLevelRate.compareTo(levelRate) > 0){ // 上级系数不能比自身小
                        // 管理提成比例：客户年份系数 * （上级 - 自身系数）
                        BigDecimal rate = customerYearRate.multiply(pLevelRate.subtract(levelRate));
                        managementCommissionRate = rate;
                        managementCommission = amount.multiply(rate).multiply(hundredInverse);
                    }else {
                        throw new RuntimeException();
                    }
                }

            }else {
                managementCommissionRate = Optional.ofNullable(management.getCommissionRate()).orElse(BigDecimal.ZERO);
                managementCommission = levelRate.multiply(amount);
            }
            recordVO.setManagementCommissionAmount(managementCommission);
            recordVO.setManagementCommissionRate(managementCommissionRate);
        }
        return recordVO;
    }

    /**
     * 计算客户年份系数
     * @param year
     * @return
     */
    public static BigDecimal calcCustomerYearRate(Integer year){
        if (year == null ){
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(1.0 - Math.max(0,  year-1) * 0.1);
    }

    // 计算客户合作年数
    public static int calculateCooperationYears(LocalDate startDate, LocalDate today) {
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
}
