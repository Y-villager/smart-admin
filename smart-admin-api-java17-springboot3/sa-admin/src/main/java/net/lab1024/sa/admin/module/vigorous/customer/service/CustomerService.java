package net.lab1024.sa.admin.module.vigorous.customer.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.convert.FlexibleDateConverter;
import net.lab1024.sa.admin.enumeration.CustomerGroupEnum;
import net.lab1024.sa.admin.enumeration.CustomesDeclarationEnum;
import net.lab1024.sa.admin.enumeration.TransferStatusEnum;
import net.lab1024.sa.admin.module.vigorous.customer.dao.CustomerDao;
import net.lab1024.sa.admin.module.vigorous.customer.domain.entity.CustomerEntity;
import net.lab1024.sa.admin.module.vigorous.customer.domain.form.*;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.BatchUtils;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartEnumUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.module.support.dict.service.DictService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
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
    private BatchUtils batchUtils;
    @Autowired
    private DictService dictService;

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
                    .registerConverter(new FlexibleDateConverter())
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
        if (mode) {  // 追加
            // 批量插入操作
            List<BatchResult> insert = customerDao.insert(entityList);
            for (BatchResult batchResult : insert) {
                successTotal += batchResult.getParameterObjects().size();
            }
        } else {  // 覆盖
            // 执行批量更新操作
            successTotal = batchUtils.doThreadInsertOrUpdate(entityList, customerDao, "batchUpdate");
        }

        String failedPath=null;
        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            failedPath = ExcelUtils.saveFailedDataToExcel(failedDataList, CustomerExportForm.class );
        }

        String action = mode ? "追加" : "覆盖";

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg( String.format(
                "总共%d条数据，成功%s%d条，%s失败记录有：%d条。失败数据路径：%s",
                dataList.size(), action, successTotal, action, failedDataList.size(), failedPath
        ));

    }


    // 生成导入列表
    private List<CustomerEntity> createImportList(List<CustomerImportForm> dataList,
                                                     List<CustomerImportForm> failedDataList,
                                                     boolean mode) {
        List<CustomerEntity> entityList = new ArrayList<>();


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
        Map<String, String> currencyMap = dictService.keyQuery("CURRENCY_TYPE");


        for (CustomerImportForm form : dataList) {
            CustomerEntity customerEntity = convertToEntity(form, keyMap, salespersonMap,currencyMap, failedDataList, mode);
            if (customerEntity != null){
                entityList.add(customerEntity);
            }

        }
        return entityList;
    }

    // 将 CustomerImportForm 转换为 CustomerEntity
    private CustomerEntity convertToEntity(CustomerImportForm form,
                                           Map<String, Long> keyMap,
                                           Map<String, Long> salespersonMap,
                                           Map<String, String> currencyMap,
                                           List<CustomerImportForm> failedDataList,
                                           Boolean mode) {
        CustomerEntity entity = new CustomerEntity();
        StringBuilder errorMsg = new StringBuilder();
        if (form.getCustomerCode() == null){
            errorMsg.append("没有客户编码，不允许导入；");
            failedDataList.add(form);
        }

        String currencyType = currencyMap.get(form.getCurrencyType());

        // 根据 mode 的值简化条件判断，true为追加
        if (mode){
            if (keyMap.containsKey(form.getCustomerCode())){
                errorMsg.append("追加模式: 系统已存在该编码的客户；");
                failedDataList.add(form);
            }
            if (!salespersonMap.containsKey(form.getSalespersonName())){
                errorMsg.append("没有该业务员；");
                failedDataList.add(form);
            }
            // 获取编码
            if (currencyType == null){
                errorMsg.append("币别系统不存在：").append(form.getCurrencyType()).append(";");
                failedDataList.add(form);
            }

        }else {
            if (!keyMap.containsKey(form.getCustomerCode())){
                errorMsg.append("覆盖模式：系统中不存在该编码的客户；");
                failedDataList.add(form);
            }
        }

        if (!errorMsg.isEmpty()){
            form.setErrorMsg(errorMsg.toString());
            return null;
        }

        entity.setCustomerName(form.getCustomerName()); // 客户名
        entity.setShortName(form.getShortName());   // 客户简称
        entity.setCountry(form.getCountry());   // 国家
        entity.setCreateDate(form.getCreateDate());   // 金蝶-创建日期
        entity.setFirstOrderDate(form.getOrderDate()); // 首单日期
        entity.setCustomerCode(form.getCustomerCode()); // 客户编码
        entity.setCustomerGroup(form.getCustomerGroup());   // 客户分组
        entity.setCurrencyType(currencyType); // 结算币别
        entity.setSalespersonId(salespersonMap.get(form.getSalespersonName())); // 业务员id
        // 新增转交状态 默认 自主开发
        if (form.getTransferStatus() != null){
            entity.setTransferStatus(form.getTransferStatus()); // 转交状态
        }else {
            entity.setTransferStatus(TransferStatusEnum.INDEPENDENTLY.getValue());
        }

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<CustomerExportForm> exportCustomers(CustomerQueryForm queryForm) {
//        List<CustomerEntity> entityList = customerDao.selectList(null);
        List<CustomerVO> entityList = customerDao.queryPage(null,queryForm);

        Set<Long> salespersonIds = new HashSet<>();
        for (CustomerVO customerVO : entityList) {
            salespersonIds.add(customerVO.getSalespersonId());
        }
        Map<Long, String> salespersonMap = salespersonService.getSalespersonNamesByIds(salespersonIds);


        return entityList.stream()
                .map(e ->
                        CustomerExportForm.builder()
                                .customerCode(e.getCustomerCode())
                                .customerName(e.getCustomerName())
                                .shortName(e.getShortName())
                                .orderDate(ExcelUtils.convertLocalDateToString(e.getFirstOrderDate()))
                                .country(e.getCountry())
                                .country(e.getCountry())
                                .customerGroup(SmartEnumUtil.getEnumDescByValue(e.getCustomerGroup(), CustomerGroupEnum.class))
                                .currencyType(e.getCurrencyType())
                                .transferStatus(SmartEnumUtil.getEnumDescByValue(e.getTransferStatus(), TransferStatusEnum.class))
                                .salespersonName(salespersonMap.get(e.getSalespersonId()))
                                .isCustomsDeclaration(SmartEnumUtil.getEnumDescByValue(e.getIsCustomsDeclaration(), CustomesDeclarationEnum.class))
                                .createDate(e.getCreateDate())
                                .build()
                )
                .collect(Collectors.toList());
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
