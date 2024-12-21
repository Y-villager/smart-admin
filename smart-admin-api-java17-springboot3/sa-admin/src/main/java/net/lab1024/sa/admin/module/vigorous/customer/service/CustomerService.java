package net.lab1024.sa.admin.module.vigorous.customer.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.system.login.service.LoginService;
import net.lab1024.sa.admin.module.vigorous.customer.dao.CustomerDao;
import net.lab1024.sa.admin.module.vigorous.customer.domain.entity.CustomerEntity;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerAddForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerImportForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerQueryForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.CustomerUpdateForm;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerExcelVO;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerVO;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.entity.FirstOrderEntity;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.result.InsertResult;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    private LoginService loginService;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<CustomerVO> queryPage(CustomerQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<Long> ids = customerDao.getCustomerIdByCustomerName(queryForm.getCustomerName());
        if (ids.size() == 1){
            queryForm.setCustomerId(ids.get(0));
        }
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
    public ResponseDTO<String> importCustomer(MultipartFile file) {
        List<CustomerImportForm> dataList;
        List<CustomerImportForm> failedDataList = new ArrayList<>();
        List<CustomerEntity> entityList = new ArrayList<>();

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
        for (CustomerImportForm form : dataList) {

            // 将有效的记录转换为实体
            CustomerEntity entity = convertToEntity(form);
            if (entity == null) {
                failedDataList.add(form);
                continue;
            }
            entityList.add(entity);
        }
        // 批量插入有效数据
        List<BatchResult> insert = customerDao.insert(entityList);

        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            File failedFile = saveFailedDataToExcel(failedDataList);
        }

        if (insert.isEmpty()){
            return ResponseDTO.okMsg("全部导入失败");
        }


        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + insert.get(0).getParameterObjects().size() + "条，导入失败记录有："+failedDataList.size()+"条" );
    }

    // 将 CustomerImportForm 转换为 CustomerEntity
    private CustomerEntity convertToEntity(CustomerImportForm form) {
        CustomerEntity entity = new CustomerEntity();

        List<Long> salespersonIds = salespersonService.getSalespersonIdByName(form.getSalespersonName());
        if (salespersonIds.size() > 1){
            form.setErrorMsg("有业务员同名");
            return null;
        }else if (salespersonIds.isEmpty()){
            form.setErrorMsg("没有找到业务员");
            return null;
        }

        List<CustomerEntity> customers = customerDao.queryByCustomerCode(form.getCustomerCode());
        if (!customers.isEmpty()){
            form.setErrorMsg("客户编码重复，客户已存在");
            return null;
        }

        entity.setCustomerName(form.getCustomerName());
        entity.setShortName(form.getShortName());
        entity.setCountry(form.getCountry());
        entity.setCustomerCode(form.getCustomerCode());
        entity.setCustomerGroup(form.getCustomerGroup());
        entity.setSalespersonId(salespersonIds.get(0));

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<CustomerExcelVO> exportCustomers() {
        List<CustomerEntity> entityList = customerDao.selectList(null);
        return entityList.stream()
                .map(e ->
                        {
//                            String salespersonName = salespersonService.getSalespersonNameById(e.getSalespersonId());
//                            if (salespersonName==null){
//                                return null;
//                            }
                            return CustomerExcelVO.builder()
                                    .customerCode(e.getCustomerCode())
                                    .customerName(e.getCustomerName())
                                    .shortName(e.getShortName())
                                    .country(e.getCountry())
                                    .customerGroup(e.getCustomerGroup())
                                    .salespersonName(salespersonService.getSalespersonNameById(e.getSalespersonId()))
                                    .build();
                        }
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


    /**
     * 查询首单id为null
     * @return
     */
    public List<CustomerVO> getCustomerOfFONull() {
        return customerDao.getCustomerOfFONull();
    }


    @Transactional
    public InsertResult updateFirstOrderIds(List<Long> customerIdToUpdate, List<FirstOrderEntity> insertedFirstOrderIds) {
        if (customerIdToUpdate != null && insertedFirstOrderIds != null && customerIdToUpdate.size() == insertedFirstOrderIds.size()) {
            int batchSize = 500; // 每批处理 500 条
            SqlSession sqlSession = null;

            try {
                // 使用 ExecutorType.BATCH 提高批量执行性能
                sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
                CustomerDao batchCustomerDao = sqlSession.getMapper(CustomerDao.class);

                // 分批次执行
                for (int i = 0; i < customerIdToUpdate.size(); i += batchSize) {
                    int end = Math.min(i + batchSize, customerIdToUpdate.size());
//                    List<Long> subCustomerIdToUpdate = customerIdToUpdate.subList(i, end);
                    List<FirstOrderEntity> subInsertedFirstOrderIds = insertedFirstOrderIds.subList(i, end);

                    // 执行每批次的更新操作
                    batchCustomerDao.updateFirstOrderIdsBatch(subInsertedFirstOrderIds);
                }

                // 提交批量操作
                sqlSession.commit();

                // 返回操作结果
                return new InsertResult(true, "批量更新成功");

            } catch (Exception e) {
                // 发生异常时回滚事务
                if (sqlSession != null) {
                    sqlSession.rollback();
                }
                // 记录异常并抛出
                return new InsertResult(false, "批量更新失败: " + e.getMessage());

            } finally {
                // 关闭 sqlSession
                if (sqlSession != null) {
                    sqlSession.close();
                }
            }
        }
        return new InsertResult(false, "输入参数错误");
    }

    public CustomerEntity queryById(Long customerId) {
        return customerDao.selectById(customerId);
    }
}
