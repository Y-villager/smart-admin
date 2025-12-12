package net.lab1024.sa.admin.module.vigorous.receivables.service;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import net.lab1024.sa.admin.convert.LocalDateConverter;
import net.lab1024.sa.admin.module.vigorous.customer.service.CustomerService;
import net.lab1024.sa.admin.module.vigorous.receivables.dao.ReceivablesDao;
import net.lab1024.sa.admin.module.vigorous.receivables.dao.ReceivablesDetailsDao;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesAddForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesImportForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesQueryForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesUpdateForm;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.vo.ReceivablesVO;
import net.lab1024.sa.admin.module.vigorous.res.ValidationResult;
import net.lab1024.sa.admin.module.vigorous.salesperson.service.SalespersonService;
import net.lab1024.sa.admin.util.BatchUtils;
import net.lab1024.sa.admin.util.ExcelUtils;
import net.lab1024.sa.admin.util.ValidationUtils;
import net.lab1024.sa.base.common.domain.PageResult;
import net.lab1024.sa.base.common.domain.ResponseDTO;
import net.lab1024.sa.base.common.exception.BusinessException;
import net.lab1024.sa.base.common.util.SmartBeanUtil;
import net.lab1024.sa.base.common.util.SmartPageUtil;
import net.lab1024.sa.base.module.support.dict.service.DictService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.executor.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static cn.dev33.satoken.SaManager.log;

/**
 * 应收单 Service
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Service
public class ReceivablesService {

    @Resource
    private ReceivablesDao receivablesDao;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private SalespersonService salespersonService;
    @Autowired
    private DictService dictService;

    @Autowired
    private BatchUtils batchUtils;

    @Autowired
    private ValidationUtils validationUtils;

    @Qualifier("receivablesDetailsDao")
    @Autowired
    private ReceivablesDetailsDao receivablesDetailsDao;

    /**
     * 分页查询
     *
     * @param queryForm
     * @return
     */
    public PageResult<ReceivablesVO> queryPage(ReceivablesQueryForm queryForm) {
        Page<?> page = SmartPageUtil.convert2PageQuery(queryForm);
        List<ReceivablesVO> list = receivablesDao.queryPage(page, queryForm);

        Map<Long, String> salespersonIdNameMap = salespersonService.getSalespersonIdNameMap();
        for (ReceivablesVO record : list) {
            record.setSalespersonName(salespersonIdNameMap.get(record.getSalespersonId()));
        }
        PageResult<ReceivablesVO> pageResult = SmartPageUtil.convert2PageResult(page, list);
        return pageResult;
    }

    /**
     * 添加
     */
    public ResponseDTO<String> add(ReceivablesAddForm addForm) {
        ReceivablesEntity receivablesEntity = SmartBeanUtil.copy(addForm, ReceivablesEntity.class);
        receivablesDao.insert(receivablesEntity);
        return ResponseDTO.ok();
    }

    /**
     * 更新
     *
     * @param updateForm
     * @return
     */
    public ResponseDTO<String> update(ReceivablesUpdateForm updateForm) {
        ReceivablesEntity receivablesEntity = SmartBeanUtil.copy(updateForm, ReceivablesEntity.class);
        receivablesDao.updateById(receivablesEntity);
        return ResponseDTO.ok();
    }

    /**
     * 批量删除
     *
     * @param idList
     * @return
     */
    public ResponseDTO<String> batchDelete(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)){
            return ResponseDTO.ok();
        }

        receivablesDao.deleteBatchIds(idList);
        return ResponseDTO.ok();
    }

    /**
     * 单个删除
     */
    public ResponseDTO<String> delete(Integer receivablesId) {
        if (null == receivablesId){
            return ResponseDTO.ok();
        }

        receivablesDao.deleteById(receivablesId);
        return ResponseDTO.ok();
    }

    /**
     * 导入
     *
     * @param file 上传文件
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseDTO<String> importReceivables(MultipartFile file, Boolean mode, Boolean hasMaterial) {
        List<ReceivablesImportForm> dataList;
        List<ReceivablesImportForm> failedDataList = Collections.synchronizedList(new ArrayList<>());

        try {
            dataList = EasyExcel.read(file.getInputStream()).head(ReceivablesImportForm.class)
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

        // 按单据编号分组
        Map<String, List<ReceivablesImportForm>> groupMap = dataList.stream()
                .filter(form -> form.getBillNo() != null)
                .collect(Collectors.groupingBy(ReceivablesImportForm::getBillNo));


        // 数据分类转换
        Set<ReceivablesEntity> entityList = createImportList(dataList, failedDataList, mode);

        // 应收表记录数
        int receivablesTotal = 0;
        // 物料记录数
        int detailsTotal = 0;

        // 提取所有物料明细并插入
        List<ReceivablesDetailsEntity> allMaterials = extractAllMaterials(entityList);

        if(entityList != null && !entityList.isEmpty()){
            if (mode) {  // 追加
                // 批量插入操作
                List<BatchResult> materialInsert = null;
                List<BatchResult> insert = receivablesDao.insert(entityList);

                //
                for (BatchResult batchResult : insert) {
                    receivablesTotal += batchResult.getParameterObjects().size();
                }
                if (!allMaterials.isEmpty() && hasMaterial) {
                    materialInsert = receivablesDetailsDao.insert(allMaterials);
                    for (BatchResult batchResult : materialInsert) {
                        detailsTotal += batchResult.getParameterObjects().size();
                    }
                }

            } else {  // 覆盖
                // 执行批量更新操作
                receivablesTotal = batchUtils.doThreadInsertOrUpdate(entityList.stream().toList(), receivablesDao, "batchUpdate", true);
                if (!allMaterials.isEmpty() && hasMaterial) {
                    detailsTotal = batchUtils.doThreadInsertOrUpdate(allMaterials, receivablesDetailsDao, "batchInsertOrUpdate", true);
                }
//                try {
//                    if (receivablesTotal != entityList.size()) {
//                        throw new RuntimeException("应收数据错误，期望导入 " + entityList.size() + " 条，实际导入 " + receivablesTotal + " 条");
//                    } else if (detailsTotal != allMaterials.size()) {
//                        throw new RuntimeException("应收物料数据错误，期望导入 " + allMaterials.size() + " 条，实际导入 " + detailsTotal + " 条");
//                    }
//                }catch (Exception e){
//                    return ResponseDTO.error(SystemErrorCode.SYSTEM_ERROR, "系统错误，不允许导入");
//                }
            }
        }

        String failed_data_path=null;
        if (!failedDataList.isEmpty()) {
            // 创建并保存失败的数据文件
            failed_data_path = ExcelUtils.saveFailedDataToExcel(failedDataList, ReceivablesImportForm.class );
        }

        String message = new StringBuilder()
                .append("总共").append(dataList.size()).append("条数据，")
                .append("成功导入").append(receivablesTotal).append("条应收记录、")
                .append(detailsTotal).append("条物料明细记录，")
                .append("导入失败").append(failedDataList.size()).append("条")
                .toString();

        return ResponseDTO.okMsg(message, failed_data_path);
    }

    // 提取明细中所有材料
    private List<ReceivablesDetailsEntity> extractAllMaterials(Set<ReceivablesEntity> entityList) {
        return entityList.stream().map(ReceivablesEntity::getMaterials)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    // 生成导入列表
    private Set<ReceivablesEntity> createImportList(List<ReceivablesImportForm> dataList,
                                                     List<ReceivablesImportForm> failedDataList,
                                                     boolean mode) {
        // 应收表
        Set<ReceivablesEntity> receivablesList = new HashSet<>();
        // 应收物料表
        Set<ReceivablesDetailsEntity> materialList = new HashSet<>();

        Set<String> billNos = new HashSet<>();
        Set<String> salespersonNames = new HashSet<>();
        Set<String> customerNames = new HashSet<>();
        // 物料
        List<ReceivablesDetailsEntity> allMaterials  = new ArrayList<>();

        for (ReceivablesImportForm form : dataList) {
            billNos.add(form.getBillNo());
            salespersonNames.add(form.getSalespersonName());
            customerNames.add(form.getCustomerName());
        }

        // 存在的billNo
        Set<String> existingBillNo = receivablesDao.getExistingBillNo(billNos);

        // 客户映射
        Map<String, Long> customerMap = customerService.queryByCustomerNames(customerNames);
        // 业务员映射
        Map<String, Long> salespersonMap = salespersonService.getSalespersonsByNames(salespersonNames);
        // 币别映射
        Map<String, String> currencyMap = dictService.keyQuery("CURRENCY_TYPE");

        // 按单据分组处理
        Map<String, List<ReceivablesImportForm>> groupedByBillNo = dataList.stream()
                .collect(Collectors.groupingBy(ReceivablesImportForm::getBillNo));

        for (Map.Entry<String, List<ReceivablesImportForm>> entry : groupedByBillNo.entrySet()) {
            String billNo = entry.getKey();
            List<ReceivablesImportForm> billData = entry.getValue();

            try {
                // 处理应收主表数据（取第一条，因为主表信息相同）
                ReceivablesImportForm mainForm = billData.get(0);

                // 应收实例转换
                ReceivablesEntity receivablesEntity = convertAndValidate(mainForm, existingBillNo, salespersonMap, customerMap, currencyMap, failedDataList, mode);

                // 处理物料明细数据
                List<ReceivablesDetailsEntity> billItems = convertToItemEntities(billData, billNo, failedDataList);

                // 设置关联关系
                if (receivablesEntity != null) {
                    receivablesList.add(receivablesEntity);

                    receivablesEntity.setMaterials(billItems);
                    materialList.addAll(billItems);
                }else {
                    failedDataList.add(mainForm);
                }
            } catch (Exception e) {
                // 处理失败，将所有该单据的数据加入失败列表
                failedDataList.addAll(billData);
                // 记录错误信息
                log.error("导入单据失败，单据号: {}", billNo, e);
            }
        }
        return receivablesList;
    }

    /**
     * 收集出库单-发货单多对多关系
     */
    private void collectReceivableRelations(
            List<ReceivablesImportForm> forms,
            String billNo,
            Map<String, Set<String>> relations) {

        Set<String> originBillNos = forms.stream()
                .map(ReceivablesImportForm::getOriginBillNo)
                .filter(org.apache.commons.lang3.StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        if (!originBillNos.isEmpty()) {
            relations.put(billNo, originBillNos);
        }
    }

    /**
     * 转换物料明细实体列表
     */
    private List<ReceivablesDetailsEntity> convertToItemEntities(List<ReceivablesImportForm> billData, String billNo,
                                                                  List<ReceivablesImportForm> failedDataList) {
        List<ReceivablesDetailsEntity> items = new ArrayList<>();
        for (ReceivablesImportForm form : billData) {
            // 跳过没有物料信息的数据（理论上不应该有）
            if (StringUtils.isBlank(form.getMaterialCode()) && StringUtils.isBlank(form.getMaterialName())) {
                continue;
            }
            if (StringUtils.isBlank(form.getMaterialName())) {
                form.setErrorMsg("缺少物料名称");
                failedDataList.add(form);
                continue;
            }
            if (StringUtils.isBlank(form.getSaleUnit())) {
                form.setErrorMsg("缺少销售单位");
                failedDataList.add(form);
                continue;
            }
            if (form.getSaleQuantity() == null) {
                form.setErrorMsg("缺少销售数量");
                failedDataList.add(form);
                continue;
            }
            if (form.getSerialNum() == null) {
                form.setErrorMsg("序号不能为空");
                failedDataList.add(form);
                continue;
            }

            ReceivablesDetailsEntity item = new ReceivablesDetailsEntity();

            item.setOriginBillNo(form.getBillNo().trim()); // 物料明细源单
            item.setMaterialCode(form.getMaterialCode());  // 物料编码
            item.setMaterialName(form.getMaterialName());  // 物料编码
            item.setSerialNum(form.getSerialNum());  // 序号
            item.setSaleUnit(form.getSaleUnit());         // 单位
            item.setSaleQuantity(form.getSaleQuantity()); // 数量

            items.add(item);
        }

        return items;
    }

    /**
     * 检验并转换成实体
     * @param form
     * @param salespersonMap
     * @param customerMap
     * @param currencyMap
     * @param failedDataList
     * @param mode true覆盖，false追加
     * @return
     */
    private ReceivablesEntity convertAndValidate(ReceivablesImportForm form,
                                          Set<String> existingBillNo,
                                          Map<String, Long> salespersonMap,
                                          Map<String, Long> customerMap,
                                          Map<String, String> currencyMap,
                                          List<ReceivablesImportForm> failedDataList,
                                              boolean mode) {
        List<String> errorMessages = new ArrayList<>();

        // 1. 验证单据编号
        if (!validationUtils.validateBillNo(form.getBillNo(), existingBillNo, mode, errorMessages)) {
            form.setErrorMsg(errorMessages.toString());
            failedDataList.add(form);
            return null;
        }

        // 2. 验证客户
        if (!validationUtils.validateCustomer(form.getCustomerName(), customerMap, errorMessages)) {
            form.setErrorMsg(errorMessages.toString());
            failedDataList.add(form);
            return null;
        }


        // 3. 验证销售员
        if (!validationUtils.validateSalesperson(form.getSalespersonName(), salespersonMap, errorMessages)) {
            form.setErrorMsg(errorMessages.toString());
            failedDataList.add(form);
            return null;
        }

        // 4.源单
        if (form.getOriginBillNo() == null){
            form.setErrorMsg("缺少源单编号");
            failedDataList.add(form);
            return null;
        }

        // 5.币别
        String currencyType = currencyMap.get(form.getCurrencyType());
        if (currencyType == null){
            form.setErrorMsg("币别系统不存在："+form.getCurrencyType());
            failedDataList.add(form);
            return null;
        }

        // 6.转换日期格式
        if (form.getReceivablesDate().contains("-")) {
        } else if (form.getReceivablesDate().contains("/")) {
        }

        // 7. 检查汇率
        if (form.getExchangeRate() == null ){
            form.setErrorMsg("汇率不能为空");
            return null;
        } else if (form.getExchangeRate().compareTo(BigDecimal.ZERO) == 0) {
            form.setErrorMsg("汇率不能为0");
            return null;
        }

        // 8. 检查应收金额
        ValidationResult validateAmount = ValidationUtils.validateAmount(form.getAmount(), form.getExchangeRate(), form.getFallAmount(), 2);
        if (validateAmount.hasErrors()) {
            form.setErrorMsg(validateAmount.getErrorMsg());
            return null;
        }

        try {
            // 转换为实体
            return convertToEntity(form, customerMap, salespersonMap, currencyMap);
        } catch (Exception e) {
            form.setErrorMsg("数据转换失败，"+e.getMessage());
            failedDataList.add(form);
            return null;
        }
    }

    private ReceivablesEntity convertToEntity(ReceivablesImportForm form,
                                              Map<String, Long> customerMap,
                                              Map<String, Long> salespersonMap,
                                              Map<String, String> currencyMap) {
        ReceivablesEntity entity = new ReceivablesEntity();

        entity.setBillNo(form.getBillNo()); // 单据编号
        entity.setOriginBillNo(form.getOriginBillNo()); // 源单编号
        entity.setCustomerId( customerMap.get(form.getCustomerName())); // 客户编号
        entity.setSalespersonId(salespersonMap.get(form.getSalespersonName())); // 业务员编号
        entity.setCurrencyType(currencyMap.get(form.getCurrencyType())); // 币别
        entity.setAmount(form.getAmount()); // 金额

        entity.setFallAmount(form.getFallAmount()); // 本位币

        entity.setExchangeRate(form.getExchangeRate()); // 汇率
        entity.setPayer(form.getPayer()); // 付款方
        entity.setRate(form.getRate()); // 应收比例

        // 转换日期格式
        if (form.getReceivablesDate().contains("-")) {
            LocalDate date = LocalDate.parse(form.getReceivablesDate());
            entity.setReceivablesDate(date);
        } else if (form.getReceivablesDate().contains("/")) {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/M/d");
            LocalDate date = LocalDate.parse(form.getReceivablesDate(), fmt);
            entity.setReceivablesDate(date);
        }

        return entity;
    }

    /**
     * 导出
     * 需要修改
     */
    public List<ReceivablesVO> getAllReceivables() {
        List<ReceivablesEntity> entityList = receivablesDao.selectList(null);
//        return entityList.stream()
//                .map(e ->
//                        ReceivablesVO.builder()
//                                .build()
//                )
//                .collect(Collectors.toList());
        return null;

    }
}
