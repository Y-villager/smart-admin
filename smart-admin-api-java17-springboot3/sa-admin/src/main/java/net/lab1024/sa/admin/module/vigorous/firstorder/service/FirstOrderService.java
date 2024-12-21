package net.lab1024.sa.admin.module.vigorous.firstorder.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.customer.domain.entity.CustomerEntity;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.firstorder.dao.FirstOrderDao;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.entity.FirstOrderEntity;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderAddForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderQueryForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.form.FirstOrderUpdateForm;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo.FirstOrderExcelVO;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo.FirstOrderVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity.SalespersonEntity;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
    public ResponseDTO<String> importFirstOrder(MultipartFile file) {
        List<FirstOrderAddForm> dataList;
        List<FirstOrderAddForm> failedDataList = new ArrayList<>();
        List<FirstOrderEntity> entityList = new ArrayList<>();

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

        // 将 FirstOrderAddForm 转换为 FirstOrderEntity，同时记录失败的数据
        for (FirstOrderAddForm form : dataList) {

            // 将有效的记录转换为实体
            FirstOrderEntity entity = convertToEntity(form);
            if (entity != null) {
                entityList.add(entity);
            } else {
                // 如果转换失败，记录失败信息
                failedDataList.add(form);
            }
        }
        // 批量插入有效数据
        List<BatchResult> insert = firstOrderDao.insert(entityList);

        // 如果有失败的数据，导出失败记录到 Excel
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + insert.get(0).getParameterObjects().size() + "条，导入失败记录有："+failedDataList.size()+"条" );

    }

    // 将 FirstOrderAddForm 转换为 FirstOrderEntity
    private FirstOrderEntity convertToEntity(FirstOrderAddForm form) {
        FirstOrderEntity entity = new FirstOrderEntity();

        return entity;
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
                                    .amount(e.getAmount())
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
