package net.lab1024.sa.admin.module.vigorous.customer.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.enumeration.CustomerGroupEnum;
import net.lab1024.sa.admin.enumeration.TransferStatusEnum;
import net.lab1024.sa.admin.module.vigorous.customer.dao.CustomerDao;
import net.lab1024.sa.admin.module.vigorous.customer.domain.entity.CustomerEntity;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerAddForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerImportForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerQueryForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerUpdateForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerExcelVO;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.SplitListUtils;
import net.lab1024.sa.base.common.code.SystemErrorCode;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 顾客 Service
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Service
public class CustomerService {

    @Resource
    private CustomerDao customerDao;
    @Autowired
    private SalespersonService salespersonService;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CustomerVO> queryPage(CustomerQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        List<CustomerVO> list = customerDao.queryPage(page, queryForm);
        PageResult<CustomerVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(CustomerAddForm addForm) {
        CustomerEntity customerEntity = SmartBeanUtil.copy(addForm, CustomerEntity.class);
        customerDao.insert(customerEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(CustomerUpdateForm updateForm) {
        CustomerEntity customerEntity = SmartBeanUtil.copy(updateForm, CustomerEntity.class);
        customerDao.updateById(customerEntity);
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

        customerDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Long customerId) {
        if (null == customerId){
            return ResponseDTO.ok();
        }

        customerDao.deleteById(customerId);
        return ResponseDTO.ok();
    }

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    public ResponseDTO<String> importCustomer(MultipartFile file, Boolean mode) {
        List<CustomerImportForm> dataList;
        List<CustomerImportForm> failedDataList = new ArrayList<>();

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(CustomerImportForm.class)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            throw new BusinessException("数据格式存在问题，无法读取");
        }

        if (CollectionUtils.isEmpty(dataList)) {
            return ResponseDTO.userErrorParam("数据为空");
        }

        // 将 CustomerImportForm 转换为 CustomerEntity，同时记录失败的数据
        List<CustomerEntity> entityList  = createImportList(dataList, failedDataList, mode);

        // true 为追加模式，false为按单据编号覆盖
        int successTotal = 0;
        try {
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> insert = customerDao.insert(entityList);
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
            failed_data_path = ExcelUtils.saveFailedDataToExcel(failedDataList, CustomerImportForm.class );
        }

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + successTotal + "条，导入失败记录有："+failedDataList.size()+"条", failed_data_path );

    }


    private int doThreadUpdate(List<CustomerEntity> entityList) {
        List<CustomerEntity> updateList = new ArrayList<>();
        // 初始化线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50,
                4, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10),
                new ThreadPoolExecutor.CallerRunsPolicy());
        // 将大集合拆分成N个小集合，使用多线程操作数据
        List<List<CustomerEntity>> splitList = SplitListUtils.splitList(entityList, 1000);
        // 记录单个任务的执行次数
        CountDownLatch countDownLatch = new CountDownLatch(splitList.size());
        for (List<CustomerEntity> subList : splitList) {
            threadPool.execute(new Thread(new Runnable() {
                @Override
                public void run() {
                    customerDao.batchUpdate(subList);
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



    // 生成导入列表
    private List<CustomerEntity> createImportList(List<CustomerImportForm> dataList,
                                                     List<CustomerImportForm> failedDataList,
                                                     boolean mode) {
        Set<String> salespersonNames = new HashSet<>();
        Set<String> customerCodes = new HashSet<>();

        for (CustomerImportForm form : dataList) {
            salespersonNames.add(form.getSalespersonName());
            customerCodes.add(form.getCustomerCode());
        }

        // customer key map
        List<CustomerVO> voList = customerDao.queryByCustomerCodes(customerCodes);
        Map<String, Long> keyMap = voList
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(CustomerVO::getCustomerCode, CustomerVO::getCustomerId));


        // 业务员映射
        Map<String, Long> salespersonMap = salespersonService.getSalespersonsByNames(salespersonNames);

        return dataList.parallelStream()
                .map(form -> convertToEntity(form,keyMap, salespersonMap, failedDataList, mode))
                .filter(Objects::nonNull)
                .toList();

    }

    // 将 CustomerImportForm 转换为 CustomerEntity
    private CustomerEntity convertToEntity(CustomerImportForm form,
                                           Map<String, Long> keyMap,
                                           Map<String, Long> salespersonMap,
                                           List<CustomerImportForm> failedDataList,
                                           Boolean mode) {
        CustomerEntity entity = new CustomerEntity();
        if (form.getCustomerCode() == null){
            form.setErrorMsg("没有客户编码，不允许导入");
            failedDataList.add(form);
            return null;
        }
        // 根据 mode 的值简化条件判断，true为追加
        if (mode){
            if (keyMap.containsKey(form.getCustomerCode())){
                form.setErrorMsg("追加模式: 系统已存在该编码的客户");
                failedDataList.add(form);
                return null;
            }
        }else {
            if (!keyMap.containsKey(form.getCustomerCode())){
                form.setErrorMsg("覆盖模式：系统中不存在该编码的客户");
                failedDataList.add(form);
                return null;
            }
        }

        if (!salespersonMap.containsKey(form.getSalespersonName())){
            form.setErrorMsg("没有该业务员");
            return null;
        }
        if (form.getOrderDate()!=null){
            entity.setFirstOrderDate( LocalDate.parse(form.getOrderDate()));// 首单日期
        }
        entity.setCustomerName(form.getCustomerName()); // 客户名
        entity.setShortName(form.getShortName());   // 客户简称
        entity.setCountry(form.getCountry());   // 国家
        entity.setCustomerCode(form.getCustomerCode()); // 客户编码
        entity.setCustomerGroup(form.getCustomerGroup());   // 客户分组
        entity.setTransferStatus(form.getTransferStatus()); // 转交状态
        entity.setSalespersonId(salespersonMap.get(form.getSalespersonName())); // 业务员id

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<CustomerExcelVO> exportCustomers(CustomerQueryForm queryForm) {
//        List<CustomerEntity> entityList = customerDao.selectList(null);
        List<CustomerVO> entityList = customerDao.queryPage(null,queryForm);

        Set<Long> salespersonIds = new HashSet<>();
        for (CustomerVO customerVO : entityList) {
            salespersonIds.add(customerVO.getSalespersonId());
        }
        Map<Long, String> salespersonMap = salespersonService.getSalespersonNamesByIds(salespersonIds);


        return entityList.stream()
                .map(e ->
                        CustomerExcelVO.builder()
                                .customerCode(e.getCustomerCode())
                                .customerName(e.getCustomerName())
                                .shortName(e.getShortName())
                                .orderDate(ExcelUtils.convertLocalDateToString(e.getFirstOrderDate()))
                                .country(e.getCountry())
                                .country(e.getCountry())
                                .customerGroup(SmartEnumUtil.getEnumDescByValue(e.getCustomerGroup(), CustomerGroupEnum.class))
                                .transferStatus(SmartEnumUtil.getEnumDescByValue(e.getTransferStatus(), TransferStatusEnum.class))
                                .salespersonName(salespersonMap.get(e.getSalespersonId()))
                                .build()
                )
                .collect(Collectors.toList());

    }

    /**
     * 保存失败的数据到 Excel 文件
     */
    private File saveFailedDataToExcel(List<CustomerImportForm> failedDataList) {
        Long userId = SmartRequestUtil.getRequestUserId();
        // 构建文件保存路径
        String userFolder = "D:\\Vigorous\\"+userId+"\\";  // 假设文件夹名称是“用户编码”，可以根据需要动态生成
        File directory = new File(userFolder);

        // 如果文件夹不存在，创建文件夹
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 构建文件路径
        File file = new File(userFolder + "failed_import_data.xlsx");

        // 使用 EasyExcel 保存失败的数据到 Excel 文件
        try (OutputStream os = new FileOutputStream(file)) {
            EasyExcel.write(os, CustomerImportForm.class)
                    .sheet("失败记录")
                    .doWrite(failedDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    /*
    * 根据客户名
    * */
    public List<Long> queryByCustomerName(String customerName) {
        return customerDao.getCustomerIdByCustomerName(customerName);
    }

    public Long getCustomerIdByCustomerName(String customerName) {
        if (customerName==null)return null;
        List<Long> names = customerDao.getCustomerIdByCustomerName(customerName);
        if (names == null || names.size() != 1) {
            return -1L;
        }
        return names.get(0);

    }

    /*
    * 根据id查询客户名称
    * */
    public String getCustomerNameById(Long customerId) {
        return customerDao.getCustomerNameById(customerId);
    }

    /**
     * 查询所有顾客信息
     * @return
     */
    public List<CustomerEntity> selectAll() {
        LambdaQueryWrapper<CustomerEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.isNull(CustomerEntity::getCustomerId);
        return customerDao.selectList(queryWrapper);
    }




    public CustomerEntity queryById(Long customerId) {
        return customerDao.selectById(customerId);
    }

    /**
     * 批量查询
     * @param customerNames
     * @return
     */
    public Map<String, Long>  queryByCustomerNames(Set<String> customerNames) {
        return customerDao.queryByCustomerNames(customerNames)
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toMap(CustomerVO::getCustomerName, CustomerVO::getCustomerId));
    }

    public Map<Long, String> getCustomerNamesByIds(Set<Long> customerIds) {
        if (customerIds==null || customerIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return customerDao.getCustomerNamesByIds(customerIds).stream()
                .collect(Collectors.toMap(CustomerVO::getCustomerId, CustomerVO::getCustomerName));
    }

}
