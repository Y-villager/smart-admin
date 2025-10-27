package net.lab1024.sa.admin.module.vigorous.fsales.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.convert.LocalDateConverter;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.fsales.dao.FSalesDao;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.entity.FSalesEntity;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.form.FSalesAddForm;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.form.FSalesImportForm;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.form.FSalesQueryForm;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.form.FSalesUpdateForm;
import net.lab1024.sa.admin.module.vigorous.fsales.domain.vo.FSalesVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.BatchUtils;
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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static cn.dev33.satoken.SaManager.log;


/**
 * 发货通知单 Service
 *
 * @Author yxz
 * @Date 2025-10-23 14:11:35
 * @Copyright (c)2024 yxz
 */

@Service
public class FSalesService {

    @Resource
    private FSalesDao fSalesDao;

    @Autowired
    private BatchUtils batchUtils;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SalespersonService salespersonService;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<FSalesVO> queryPage(FSalesQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<FSalesVO> list = fSalesDao.queryPage(page, queryForm);
        PageResult<FSalesVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(FSalesAddForm addForm) {
        FSalesEntity fSalesEntity = SmartBeanUtil.copy(addForm, FSalesEntity.class);
        fSalesDao.insert(fSalesEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(FSalesUpdateForm updateForm) {
        FSalesEntity fSalesEntity = SmartBeanUtil.copy(updateForm, FSalesEntity.class);
        fSalesDao.updateById(fSalesEntity);
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

        fSalesDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long fSalesId) {
        if (null == fSalesId){
            return ResponseDTO.ok();
        }

        fSalesDao.deleteById(fSalesId);
        return ResponseDTO.ok();
    }

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importFSales(MultipartFile file, Boolean mode) {
        List<FSalesImportForm> dataList;
        List<FSalesImportForm> failedDataList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(FSalesImportForm.class)
                    .registerConverter(new LocalDateConverter())
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        // 将 FSalesImportForm 转换为 FSalesEntity，同时记录失败的数据
        List<FSalesEntity> entityList = createImportList(dataList, failedDataList, mode);

        // true 为追加模式，false为按单据编号覆盖
        int successTotal = 0;
        try {
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> insert = fSalesDao.insert(entityList);
                for (BatchResult batchResult : insert) {
                    successTotal += batchResult.getParameterObjects().size();
                }
            } else {  // 覆盖
                // 执行批量更新操作
                successTotal = batchUtils.doThreadInsertOrUpdate(entityList, fSalesDao, "batchUpdate");
                if (successTotal != entityList.size()) {
                    return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "系统出错，请联系管理员。");
                }
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
            failed_data_path = ExcelUtils.saveFailedDataToExcel(failedDataList, FSalesImportForm.class);
        }

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + successTotal + "条，导入失败记录有："+failedDataList.size()+"条", failed_data_path );

    }

    // 生成导入列表
    private List<FSalesEntity> createImportList(List<FSalesImportForm> dataList,
                                                     List<FSalesImportForm> failedDataList,
                                                     boolean mode) {

        List<FSalesEntity> entityList = new ArrayList<>();
        Set<String> billNos = new HashSet<>();
        Set<String> salespersonNames = new HashSet<>();
        Set<String> customerNames = new HashSet<>();

        for (FSalesImportForm form : dataList) {
            billNos.add(form.getBillNo());
            customerNames.add(form.getCustomerName());
            salespersonNames.add(form.getSalespersonName());
        }

        // 单据编号 map
        Set<String> existingBillNos = fSalesDao.getExistingBillNo(billNos);


        // 客户映射
        Map<String, String> customerMap = customerService.queryCustomerCodeByCustomerNames(customerNames);
        Map<String, String> salespesonMap = salespersonService.getSalespersonCodeByNames(salespersonNames);

        for (FSalesImportForm importForm : dataList) {
            FSalesEntity entity = convertAndValidate(importForm, existingBillNos, customerMap, salespesonMap, failedDataList, mode);
            if (entity != null){
                entityList.add(entity);
            }
        }

        // 需要的映射数据
        return entityList;
    }

    // 将 FSalesImportForm 转换为 SalesOrderEntity
    private FSalesEntity convertAndValidate(FSalesImportForm form,
                                                Set<String> billNos,
                                                Map<String, String> customerMap,
                                                Map<String, String> salespersonMap,
                                                List<FSalesImportForm> failedDataList,
                                                boolean mode) {
        List<String> errorMessages = new ArrayList<>();

        // 1. 验证单据编号
        if (!validateBillNo(form.getBillNo(), billNos, mode, errorMessages)) {
            form.setErrorMsg(errorMessages.toString());
            failedDataList.add(form);
            return null;
        }

        // 2. 验证客户
        if (!validateCustomer(form.getCustomerName(), customerMap, errorMessages)) {
            form.setErrorMsg(errorMessages.toString());
            failedDataList.add(form);
            return null;
        }

        // 3. 验证销售员
        if (!validateSalesperson(form.getSalespersonName(), salespersonMap, errorMessages)) {
            form.setErrorMsg(errorMessages.toString());
            failedDataList.add(form);
            return null;
        }

        try {
            // 转换为实体
            return convertToEntity(form, customerMap, salespersonMap);
        } catch (Exception e) {
            form.setErrorMsg("数据转换失败，"+e.getMessage());
            failedDataList.add(form);
            return null;
        }
    }


    /**
     * 验证单据编号
     */
    private boolean validateBillNo(String billNo, Set<String> existingBillNos,
                                   boolean updateMode, List<String> errorMessages) {
        // 1. 不能为空
        if (StringUtils.isBlank(billNo)) {
            errorMessages.add("单据编号不能为空");
            return false;
        }

        // 2. 不能重复（新增模式下）
        if (updateMode && existingBillNos.contains(billNo)) {
            errorMessages.add("单据编号已存在: " + billNo);
            return false;
        }

        // 3. 格式验证（可根据需要添加）
        if (billNo.length() > 50) {
            errorMessages.add("单据编号长度不能超过50个字符");
            return false;
        }

        return true;
    }

    /**
     * 验证客户
     */
    private boolean validateCustomer(String customerName, Map<String, String> customerMap,
                                     List<String> errorMessages) {
        // 1. 不能为空
        if (StringUtils.isBlank(customerName)) {
            errorMessages.add("客户名称不能为空");
            return false;
        }

        // 2. 必须存在
        if (!customerMap.containsKey(customerName)) {
            errorMessages.add("客户不存在: " + customerName);
            return false;
        }

        return true;
    }

    /**
     * 验证销售员
     */
    private boolean validateSalesperson(String salespersonName, Map<String, String> salespersonMap,
                                        List<String> errorMessages) {
        // 1. 不能为空
        if (StringUtils.isBlank(salespersonName)) {
            errorMessages.add("销售员名称不能为空");
            return false;
        }

        // 2. 必须存在
        if (!salespersonMap.containsKey(salespersonName)) {
            errorMessages.add("销售员不存在: " + salespersonName);
            return false;
        }

        return true;
    }


    // 将 FSalesImportForm 转换为 FSalesEntity
    private FSalesEntity convertToEntity(FSalesImportForm form,
                                                     Map<String, String> customerMap,
                                                     Map<String, String> salespersonMap) {
        FSalesEntity entity = new FSalesEntity();

        entity.setBillNo(form.getBillNo().trim()); // 单据编号
        entity.setBillDate(form.getBillDate());  // 日期
        entity.setOriginBillNo(form.getOriginBillNo()); // 源单编号

        // 关联信息
        entity.setCustomerCode(customerMap.get(form.getCustomerName().trim()));  // 客户编码
        entity.setSalespersonCode(salespersonMap.get(form.getSalespersonName().trim())); // 业务员编码

        // 系统字段
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<FSalesVO> exportFSales(FSalesQueryForm queryForm) {
        //List<FSalesVO> entityList = fSalesDao.selectList(null);
        List<FSalesVO> entityList = fSalesDao.queryPage(null,queryForm);
//        return entityList.stream()
//                .map(e ->
//                        FSalesVO.builder()
//                                .build()
//                )
//                .collect(Collectors.toList());
        return null;

    }

    private int doThreadUpdate(List<FSalesEntity> entityList) {
        List<FSalesEntity> updateList = new ArrayList<>();
        // 初始化线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50,
                4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<FSalesEntity>> splitList = SplitListUtils.splitList(entityList, 1000);
        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<FSalesEntity> subList : splitList) {
            threadPool.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("当前线程："+ Thread.currentThread().getName());
                    //fSalesDao.updateFSalesByBillNo(subList);
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
}
