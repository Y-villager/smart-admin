package net.lab1024.sa.admin.module.vigorous.sales.outbound.service;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.dao.SalesOutboundDao;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.entity.SalesOutboundEntity;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundAddForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundImportForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundQueryForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundUpdateForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<SalesOutboundVO> queryPage(SalesOutboundQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

        if (queryForm.getCustomerName() != null && !queryForm.getCustomerName().isEmpty()) {
            queryForm.setCustomerId(customerService.getCustomerIdByCustomerName(queryForm.getCustomerName()));
        }
        if (queryForm.getSalespersonName() != null && !queryForm.getSalespersonName().isEmpty()) {
            queryForm.setSalespersonId(salespersonService.getIdBySalespersonName(queryForm.getSalespersonName()));
        }
        List<SalesOutboundVO> list = salesOutboundDao.queryPage(page, queryForm);
        // 查询部门、业务员级别名称
        list.forEach(e -> {
            e.setSalespersonName(salespersonService.getSalespersonNameById(e.getSalespersonId()));
            e.setCustomerName(customerService.getCustomerNameById(e.getCustomerId()));
        });

        PageResult<SalesOutboundVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
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
        List<SalesOutboundEntity> entityList = new ArrayList<>();

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

        // 将 SalesOutboundImportForm 转换为 SalesOutboundEntity，同时记录失败的数据
        for (SalesOutboundImportForm form : dataList) {

            // 将有效的记录转换为实体
            SalesOutboundEntity entity = convertToEntity(form);

            if (form.getErrorMsg() == null) {
                entityList.add(entity);
            } else {
                // 如果转换失败，记录失败信息
                failedDataList.add(form);
            }
        }
        // 批量插入有效数据
        List<BatchResult> insert = salesOutboundDao.insert(entityList);

        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            File failedFile = saveFailedDataToExcel(failedDataList);
        }

        if (insert.isEmpty()){
            return ResponseDTO.okMsg("全部导入失败");
        }
        return ResponseDTO.okMsg("总共"+dataList.size()+"条数据，成功导入" + insert.get(0).getParameterObjects().size() + "条，导入失败记录有："+failedDataList.size()+"条" );

    }

    // 将 SalesOutboundImportForm 转换为 SalesOutboundEntity
    private SalesOutboundEntity convertToEntity(SalesOutboundImportForm form) {
        SalesOutboundEntity entity = new SalesOutboundEntity();

        if (form.getSalesBoundDate()!=null){

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/M/d");
            LocalDate date = LocalDate.parse(form.getSalesBoundDate(), fmt);
            entity.setSalesBoundDate(date);
        }

        SalesOutboundEntity salesOutbound = salesOutboundDao.queryByBillNo(form.getBillNo());
        if (salesOutbound != null) {
            form.setErrorMsg("单据编号重复，不允许导入");
            return entity;
        }
        List<Long> ids = salespersonService.getSalespersonIdByName(form.getSalespersonName());
        if (CollectionUtils.isEmpty(ids)) {
            form.setErrorMsg("找不到业务员，不允许导入");
            return entity;
        }else if (ids.size() > 1) {
            form.setErrorMsg("系统中有业务员重名");
            return entity;
        }

        List<Long> customerIds = customerService.queryByCustomerName(form.getCustomerName());
        if (CollectionUtils.isEmpty(customerIds)) {
            form.setErrorMsg("找不到客户信息，不允许导入");
            return entity;
        }else if (customerIds.size() > 1) {
            form.setErrorMsg("系统中有客户重名，不允许导入");
            return entity;
        }

        entity.setCustomerId(customerIds.get(0));
        entity.setSalespersonId(ids.get(0));
        entity.setAmount(form.getAmount());
        entity.setBillNo(form.getBillNo());

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<SalesOutboundVO> getAllSalesOutbound() {
        List<SalesOutboundEntity> goodsEntityList = salesOutboundDao.selectList(null);
        return null;

    }

    /**
     * 保存失败的数据到 Excel 文件
     */
    private File saveFailedDataToExcel(List<SalesOutboundImportForm> failedDataList) {
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
            EasyExcel.write(os, SalesOutboundImportForm.class)
                    .sheet("失败记录")
                    .doWrite(failedDataList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
