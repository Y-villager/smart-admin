package net.lab1024.sa.admin.module.vigorous.firstorder.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.customer.domain.entity.CustomerEntity;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.firstorder.dao.FirstOrderDao;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.entity.FirstOrderEntity;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderAddForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderImportForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderQueryForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderUpdateForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo.FirstOrderExcelVO;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo.FirstOrderVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity.SalespersonEntity;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.SplitListUtils;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static cn.dev33.satoken.SaManager.log;

/**
 * 客户首单信息 Service
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@Service
public class FirstOrderService {

    @Resource
    private FirstOrderDao firstOrderDao;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SalespersonService salespersonService;

    @Value("${file.excel.failed-import.failed-data-name}")
    private String failedDataName;
    @Value("${file.excel.failed-import.upload-path}")
    private String uploadPath;
    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<FirstOrderVO> queryPage(FirstOrderQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        queryForm.setCustomerId(customerService.getCustomerIdByCustomerName(queryForm.getCustomerName()));
        queryForm.setSalespersonId(salespersonService.getIdBySalespersonName(queryForm.getSalespersonName()));

        List<FirstOrderVO> list = firstOrderDao.queryPage(page, queryForm);

        PageResult<FirstOrderVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(FirstOrderAddForm addForm) {
        FirstOrderEntity firstOrderEntity = SmartBeanUtil.copy(addForm, FirstOrderEntity.class);
        firstOrderDao.insert(firstOrderEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(FirstOrderUpdateForm updateForm) {
        FirstOrderEntity firstOrderEntity = SmartBeanUtil.copy(updateForm, FirstOrderEntity.class);
        firstOrderDao.updateById(firstOrderEntity);
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

        firstOrderDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long firstOrderId) {
        if (null == firstOrderId){
            return ResponseDTO.ok();
        }

        firstOrderDao.deleteById(firstOrderId);
        return ResponseDTO.ok();
    }

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importFirstOrder(MultipartFile file, Boolean mode) {
        List<FirstOrderImportForm> dataList;
        List<FirstOrderImportForm> failedDataList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(FirstOrderAddForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        List<FirstOrderEntity> entityList = createImportList(dataList, failedDataList, mode);
        // true 为追加模式，false为按单据编号覆盖
        int successTotal = 0;
        try {
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> insert = firstOrderDao.insert(entityList);
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

        String failed_data_path=null;
        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            failed_data_path = ExcelUtils.saveFailedDataToExcel(failedDataList, FirstOrderImportForm.class, uploadPath,failedDataName );
        }

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + successTotal + "条，导入失败记录有："+failedDataList.size()+"条", failed_data_path );

    }

    private int doThreadUpdate(List<FirstOrderEntity> entityList) {
        List<FirstOrderEntity> updateList = new ArrayList<>();
        // 初始化线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50,
                4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<FirstOrderEntity>> splitList = SplitListUtils.splitList(entityList, 1000);
        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<FirstOrderEntity> subList : splitList) {
            threadPool.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("当前线程："+ Thread.currentThread().getName());
//                    firstOrderDao.updateFirstOrderByBillNo(subList);
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

    // 将 FirstOrderAddForm 转换为 FirstOrderEntity
    private FirstOrderEntity convertToEntity(FirstOrderImportForm form,
                                             Map<String, Long> keyMap,
                                             List<FirstOrderImportForm> failedDataList,
                                             Boolean mode) {
        FirstOrderEntity entity = new FirstOrderEntity();

        return entity;
    }

    private List<FirstOrderEntity> createImportList(List<FirstOrderImportForm> dataList,
                                                     List<FirstOrderImportForm> failedDataList,
                                                     boolean mode) {
        Set<String> salespersonNames = new HashSet<>();
        Set<String> customerCodes = new HashSet<>();

        for (FirstOrderImportForm form : dataList) {
            salespersonNames.add(form.getSalespersonName());
            customerCodes.add(form.getCustomerCode());
        }
        // 单据编号 map
        List<FirstOrderVO> voList = firstOrderDao.queryByCustomerCodes(customerCodes);
        Map<String, Long> keyMap = voList
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(FirstOrderVO::getCustomerCode, FirstOrderVO::getFirstOrderId));

        // 业务员映射
        Map<String, Long> salespersonMap = salespersonService.getSalespersonsByNames(salespersonNames);

        return dataList.parallelStream()
                .map(form -> convertToEntity(form, keyMap, failedDataList, mode))
                .toList();
    }


    /**
     * 导出
     * 需要修改
     */
    public List<FirstOrderExcelVO> getAllFirstOrder() {
        List<FirstOrderEntity> entityList = firstOrderDao.selectList(null);
        return entityList.stream()
                .map(e ->
                        {
                            SalespersonEntity salesperson = salespersonService.queryById(e.getSalespersonId());
                            CustomerEntity customer = customerService.queryById(e.getCustomerId());

                            return FirstOrderExcelVO.builder()
                                    .orderDate(e.getOrderDate())
                                    .salespersonName(salesperson.getSalespersonName())
                                    .salespersonCode(salesperson.getSalespersonCode())
                                    .customerCode(customer.getCustomerCode())
                                    .customerName(customer.getCustomerName())
                                    .billNo(e.getBillNo())
                                    .build();
                        }
                )
                .collect(Collectors.toList());

    }

    public void insertBatchFirstOrder(List<FirstOrderEntity> initList) {
        try {
            firstOrderDao.insert(initList);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
