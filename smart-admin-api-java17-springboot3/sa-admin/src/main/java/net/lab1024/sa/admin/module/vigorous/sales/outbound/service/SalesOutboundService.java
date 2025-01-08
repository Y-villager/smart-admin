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
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundExcelVO;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo.SalesOutboundVO;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.common.util.SmartRequestUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
            String file1 = ExcelUtils.saveFailedDataToExcel(failedDataList, SalesOutboundImportForm.class, uploadPath, failedDataName);
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
                        .salespersonName(e.getSalespersonName())
                        .billNo(e.getBillNo())
                        .salesBoundDate(e.getSalesBoundDate())
                        .amount(e.getAmount())
                        .customerName(e.getCustomerName())
                        .build()
                ).collect(Collectors.toList());
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
        File file = new File(userFolder + "outbound_failed_import_data.xlsx");

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

    /**
     * 查询客户首单信息
     * @param customerId
     * @return
     */
    public SalesOutboundVO queryFirstOrderOfCustomer(Long customerId) {
        return salesOutboundDao.queryFirstOrderOfCustomer(customerId);
    }

    public PageResult<SalesOutboundVO> queryPageWithReceivables(SalesOutboundQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);

//        if (queryForm.getCustomerName() != null && !queryForm.getCustomerName().isEmpty()) {
//            queryForm.setCustomerId(customerService.getCustomerIdByCustomerName(queryForm.getCustomerName()));
//        }
//        if (queryForm.getSalespersonName() != null && !queryForm.getSalespersonName().isEmpty()) {
//            queryForm.setSalespersonId(salespersonService.getIdBySalespersonName(queryForm.getSalespersonName()));
//        }

        List<SalesOutboundVO> list = salesOutboundDao.queryPageWithReceivables(page, queryForm);

        // 获取所有需要的业务员和客户信息
        Set<Long> salespersonIds = list.stream()
                .map(SalesOutboundVO::getSalespersonId)
                .collect(Collectors.toSet());
        Set<Long> customerIds = list.stream()
                .map(SalesOutboundVO::getCustomerId)
                .collect(Collectors.toSet());

        Map<Long, String> salespersonNames = salespersonService.getSalespersonNamesByIds(salespersonIds);
        Map<Long, String> customerNames = customerService.getCustomerNamesByIds(customerIds);


        // 填充业务员和客户名称
        list.forEach(e -> {
            e.setSalespersonName(salespersonNames.get(e.getSalespersonId()));
            e.setCustomerName(customerNames.get(e.getCustomerId()));
        });

        PageResult<SalesOutboundVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }
}
