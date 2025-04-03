package net.lab1024.sa.admin.module.vigorous.commission.calc.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.enumeration.CommissionTypeEnum;
import net.lab1024.sa.admin.module.vigorous.commission.calc.dao.CommissionRecordDao;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity.CommissionRecordEntity;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form.*;
import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo.CommissionRecordVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.dao.SalesOutboundDao;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.SplitListUtils;
import net.lab1024.sa.admin.util.ThreadPoolUtils;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CommissionRecordVO> queryPage(CommissionRecordQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        Map<Long, String> salespersonIdNameMap = salespersonService.getSalespersonIdNameMap();
        List<CommissionRecordVO> list = commissionRecordDao.queryPage(page, queryForm);
        for (CommissionRecordVO record : list) {
            record.setSalespersonName(salespersonIdNameMap.get(record.getSalespersonId()));
        }
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
     * 导出
     * @param queryForm
     * @return
     */
    public Map<String, Collection<?>> exportCommissionRecord(CommissionRecordQueryForm queryForm) {
        Map<String, Collection<?>> resList = new HashMap<>();

        // 获取销售人员 ID 到名称的映射
        Map<Long, String> salespersonIdNameMap = salespersonService.getSalespersonIdNameMap();
        // 查询提成记录
        List<CommissionRecordVO> commissionList = commissionRecordDao.queryPage(null, queryForm);

        // 使用 groupingBy 进行分组，根据 commissionType 进行分类
        Map<String, List<CommissionRecordExportForm>> groupedResult = commissionList.stream()
                .map(e -> {
                    // 创建 CommissionRecordExportForm
                    return CommissionRecordExportForm.builder()
                            .orderDate(e.getOrderDate())
                            .salesBillNo(e.getSalesBillNo())
                            .customerName(e.getCustomerName())
                            .customerCode(e.getCustomerCode())
                            .salespersonName(salespersonIdNameMap.get(e.getSalespersonId()))
                            .salespersonLevelRate(e.getCurrentSalespersonLevelRate())
                            .currentParentName(salespersonIdNameMap.get(e.getCurrentParentId()))
                            .currentParentLevelRate(e.getCurrentParentLevelRate())
                            .firstOrderDate(e.getFirstOrderDate())
                            .adjustedFirstOrderDate(e.getAdjustedFirstOrderDate())
                            .customerYear(e.getCustomerYear())
                            .customerYearRate(e.getCustomerYearRate())
                            .salesAmount(e.getSalesAmount())
                            .currencyType(e.getCurrencyType())
                            .commissionRate(e.getCommissionRate())
                            .commissionAmount(e.getCommissionAmount())
                            .isCustomsDeclaration(e.getIsCustomsDeclaration() == 0 ? "不报关" : "报关")
                            .isTransfer(e.getIsTransfer() == 0 ? "自主开发" : "转交客户")
                            .commissionType(e.getCommissionType()) // 保留 commissionType
                            .build();
                })
                .collect(Collectors.groupingBy(e -> {
                    // 根据 commissionType 分类
                    if (e.getCommissionType().equals(CommissionTypeEnum.BUSINESS.getValue())) {
                        return "业务提成";
                    } else if (e.getCommissionType().equals(CommissionTypeEnum.MANAGEMENT.getValue())) {
                        return "管理提成";
                    } else {
                        return "其他、未知";
                    }
                }));

        // 将分组后的结果添加到 resList 中
        groupedResult.forEach((key, value) -> {
            if (!value.isEmpty()) {
                resList.put(key, value);
            }
        });
        return resList;
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

}
