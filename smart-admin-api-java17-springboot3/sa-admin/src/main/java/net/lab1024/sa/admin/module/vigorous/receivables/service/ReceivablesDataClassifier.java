package net.lab1024.sa.admin.module.vigorous.receivables.service;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import net.lab1024.sa.admin.module.vigorous.receivables.dao.ReceivablesDao;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesImportForm;
import net.lab1024.sa.admin.module.vigorous.receivables.res.ClassificationResult;
import net.lab1024.sa.admin.module.vigorous.receivables.res.FailedData;
import net.lab1024.sa.base.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class ReceivablesDataClassifier {

    @Autowired
    private ReceivablesDao receivablesDao;

    /**
     * 将Excel数据分类转换为插入数据
     */
    public ClassificationResult classifyAndConvertData(List<ReceivablesImportForm> dataList, Boolean mode) {
        ClassificationResult result = new ClassificationResult();

        if (CollectionUtils.isEmpty(dataList)) {
            return result;
        }

        // 1. 数据预处理和验证
        preprocessAndValidateData(dataList, result);

        // 2. 按单据编号分组
        Map<String, List<ReceivablesImportForm>> billGroup = groupDataByBillNo(dataList, result);

        // 3. 业务规则验证
        validateBusinessRules(billGroup, result, mode);

        // 4. 转换为实体数据
        convertToEntities(billGroup, result);

        return result;
    }

    /**
     * 数据预处理和验证
     */
    private void preprocessAndValidateData(List<ReceivablesImportForm> dataList, ClassificationResult result) {
        for (int i = 0; i < dataList.size(); i++) {
            ReceivablesImportForm form = dataList.get(i);
            int rowIndex = i + 2; // Excel行号（表头+1）

            try {
                // 数据清洗
                cleanFormData(form);

                // 基础验证
                List<String> errors = validateFormData(form);
                if (!errors.isEmpty()) {
                    FailedData failedData = new FailedData(rowIndex, form, String.join("; ", errors));
                    result.addFailedData(failedData);
                    continue;
                }

                result.addValidData(form);

            } catch (Exception e) {
                log.error("数据预处理失败，行{}: {}", rowIndex, e.getMessage());
                FailedData failedData = new FailedData(rowIndex, form, "数据预处理失败: " + e.getMessage());
                result.addFailedData(failedData);
            }
        }
    }

    /**
     * 数据清洗
     */
    private void cleanFormData(ReceivablesImportForm form) {
        // 字符串去空格
        if (form.getBillNo() != null) {
            form.setBillNo(form.getBillNo().trim());
        }
        if (form.getOriginBillNo() != null) {
            form.setOriginBillNo(form.getOriginBillNo().trim());
        }
        if (form.getSalespersonName() != null) {
            form.setSalespersonName(form.getSalespersonName().trim());
        }
        if (form.getCustomerName() != null) {
            form.setCustomerName(form.getCustomerName().trim());
        }
        if (form.getCurrencyType() != null) {
            form.setCurrencyType(form.getCurrencyType().trim());
        }
        if (form.getPayer() != null) {
            form.setPayer(form.getPayer().trim());
        }
        if (form.getMaterialCode() != null) {
            form.setMaterialCode(form.getMaterialCode().trim());
        }
        if (form.getMaterialName() != null) {
            form.setMaterialName(form.getMaterialName().trim());
        }
        if (form.getSaleUnit() != null) {
            form.setSaleUnit(form.getSaleUnit().trim());
        }

        // 金额精度处理
        if (form.getAmount() != null) {
            form.setAmount(form.getAmount().setScale(2, RoundingMode.HALF_UP));
        }
        if (form.getExchangeRate() != null) {
            form.setExchangeRate(form.getExchangeRate().setScale(4, RoundingMode.HALF_UP));
        }
        if (form.getFallAmount() != null) {
            form.setFallAmount(form.getFallAmount().setScale(2, RoundingMode.HALF_UP));
        }
        if (form.getSaleQuantity() != null) {
            form.setSaleQuantity(form.getSaleQuantity());
        }
    }

    /**
     * 表单数据验证
     */
    private List<String> validateFormData(ReceivablesImportForm form) {
        List<String> errors = new ArrayList<>();

        // 必填字段验证
        if (StringUtils.isBlank(form.getBillNo())) {
            errors.add("单据编号不能为空");
        } else if (form.getBillNo().length() > 50) {
            errors.add("单据编号长度不能超过50个字符");
        }

        if (StringUtils.isBlank(form.getReceivablesDate())) {
            errors.add("业务日期不能为空");
        } else {
            try {
                LocalDate receivablesDate = convertStringToLocalDate(form.getReceivablesDate());
                if (receivablesDate.isAfter(LocalDate.now())) {
                    errors.add("业务日期不能晚于当前日期");
                }
            } catch (Exception e) {
                errors.add("业务日期格式错误，请使用yyyy-MM-dd格式");
            }
        }

        if (StringUtils.isBlank(form.getSalespersonName())) {
            errors.add("销售员不能为空");
        }

        if (StringUtils.isBlank(form.getCustomerName())) {
            errors.add("客户名称不能为空");
        }

        if (form.getAmount() == null) {
            errors.add("价税合计不能为空");
        } else if (form.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("价税合计必须大于0");
        }

        if (StringUtils.isBlank(form.getCurrencyType())) {
            errors.add("币别不能为空");
        }

        if (form.getExchangeRate() == null) {
            errors.add("汇率不能为空");
        } else if (form.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("汇率必须大于0");
        }

        // 物料信息验证
        if (StringUtils.isNotBlank(form.getMaterialCode()) && StringUtils.isBlank(form.getMaterialName())) {
            errors.add("物料编码和物料名称必须同时填写");
        }

        if (StringUtils.isBlank(form.getMaterialCode()) && StringUtils.isNotBlank(form.getMaterialName())) {
            errors.add("物料编码和物料名称必须同时填写");
        }

        return errors;
    }

    /**
     * 按单据编号分组
     */
    private Map<String, List<ReceivablesImportForm>> groupDataByBillNo(
            List<ReceivablesImportForm> dataList,
            ClassificationResult result) {

        Map<String, List<ReceivablesImportForm>> billGroup = new LinkedHashMap<>();

        for (ReceivablesImportForm form : result.getValidDataList()) {
            billGroup.computeIfAbsent(form.getBillNo(), k -> new ArrayList<>())
                    .add(form);
        }

        log.info("数据分组完成，共 {} 个单据", billGroup.size());
        return billGroup;
    }

    /**
     * 业务规则验证
     */
    private void validateBusinessRules(Map<String, List<ReceivablesImportForm>> billGroup,
                                       ClassificationResult result,
                                       Boolean mode) {

        // 检查单据编号唯一性（追加模式）
        if (Boolean.TRUE.equals(mode)) {
            checkDuplicateBillNos(billGroup, result);
        }

        // 验证每个单据组内的数据一致性
        for (Map.Entry<String, List<ReceivablesImportForm>> entry : billGroup.entrySet()) {
            String billNo = entry.getKey();
            List<ReceivablesImportForm> billItems = entry.getValue();

            try {
                validateBillGroupConsistency(billNo, billItems);
            } catch (BusinessException e) {
                // 整个单据组标记为失败
                for (ReceivablesImportForm item : billItems) {
                    FailedData failedData = new FailedData(0, item, e.getMessage());
                    result.addFailedData(failedData);
                    result.getValidDataList().remove(item);
                }
                billGroup.remove(billNo);
            }
        }
    }

    /**
     * 检查重复单据编号
     */
    private void checkDuplicateBillNos(Map<String, List<ReceivablesImportForm>> billGroup,
                                       ClassificationResult result) {

        Set<String> billNos = billGroup.keySet();
        if (CollectionUtils.isEmpty(billNos)) {
            return;
        }

        // 检查数据库中已存在的单据
        Set<String> existingBillNos = receivablesDao.getExistingBillNo(billNos);

        // 处理重复的单据
        for (String existingBillNo : existingBillNos) {
            List<ReceivablesImportForm> duplicateItems = billGroup.remove(existingBillNo);
            for (ReceivablesImportForm item : duplicateItems) {
                FailedData failedData = new FailedData(0, item, "单据编号已存在");
                result.addFailedData(failedData);
                result.getValidDataList().remove(item);
            }
        }
    }

    /**
     * 验证单据组内数据一致性
     */
    private void validateBillGroupConsistency(String billNo, List<ReceivablesImportForm> billItems) {
        if (CollectionUtils.isEmpty(billItems)) {
            throw new BusinessException("单据数据为空");
        }

        ReceivablesImportForm firstItem = billItems.get(0);

        // 验证基础信息一致性
        for (int i = 1; i < billItems.size(); i++) {
            ReceivablesImportForm currentItem = billItems.get(i);

            if (!StringUtils.equals(firstItem.getCustomerName(), currentItem.getCustomerName())) {
                throw new BusinessException("同一单据内客户名称不一致");
            }

            if (!StringUtils.equals(firstItem.getReceivablesDate(), currentItem.getReceivablesDate())) {
                throw new BusinessException("同一单据内业务日期不一致");
            }

            if (!StringUtils.equals(firstItem.getSalespersonName(), currentItem.getSalespersonName())) {
                throw new BusinessException("同一单据内销售员不一致");
            }

            if (!StringUtils.equals(firstItem.getCurrencyType(), currentItem.getCurrencyType())) {
                throw new BusinessException("同一单据内币别不一致");
            }

            if (firstItem.getExchangeRate() != null && currentItem.getExchangeRate() != null &&
                    firstItem.getExchangeRate().compareTo(currentItem.getExchangeRate()) != 0) {
                throw new BusinessException("同一单据内汇率不一致");
            }
        }

    }

    /**
     * 转换为实体数据
     */
    private void convertToEntities(Map<String, List<ReceivablesImportForm>> billGroup,
                                   ClassificationResult result) {

        for (Map.Entry<String, List<ReceivablesImportForm>> entry : billGroup.entrySet()) {
            String billNo = entry.getKey();
            List<ReceivablesImportForm> billItems = entry.getValue();

            try {
                // 创建应收单实体
                ReceivablesEntity receivablesEntity = convertToReceivablesEntity(billItems);

                // 创建物料明细实体
                List<ReceivablesDetailsEntity> materialEntities = convertToMaterialEntities(billItems, receivablesEntity);
                receivablesEntity.setMaterials(materialEntities);

                result.addReceivablesEntity(receivablesEntity);

            } catch (Exception e) {
                log.error("转换实体数据失败，单据编号: {}", billNo, e);
                for (ReceivablesImportForm item : billItems) {
                    FailedData failedData = new FailedData(0, item, "数据转换失败: " + e.getMessage());
                    result.addFailedData(failedData);
                }
            }
        }
    }

    /**
     * 转换为应收单实体
     */
    private ReceivablesEntity convertToReceivablesEntity(List<ReceivablesImportForm> billItems) {
        ReceivablesImportForm firstItem = billItems.get(0);

        ReceivablesEntity entity = new ReceivablesEntity();
        entity.setBillNo(firstItem.getBillNo());
        entity.setOriginBillNo(firstItem.getOriginBillNo());
        entity.setReceivablesDate(convertStringToLocalDate(firstItem.getReceivablesDate()));
//        entity.setSalespersonName(firstItem.getSalespersonName());
//        entity.setCustomerName(firstItem.getCustomerName());
        entity.setAmount(firstItem.getAmount());
        entity.setCurrencyType(firstItem.getCurrencyType());
        entity.setExchangeRate(firstItem.getExchangeRate());
        entity.setPayer(firstItem.getPayer());
        entity.setRate(firstItem.getRate());

        // 计算本位币金额
        if (firstItem.getFallAmount() != null) {
            entity.setFallAmount(firstItem.getFallAmount());
        } else if (firstItem.getAmount() != null && firstItem.getExchangeRate() != null) {
            entity.setFallAmount(firstItem.getAmount().multiply(firstItem.getExchangeRate())
                    .setScale(2, RoundingMode.HALF_UP));
        }

        return entity;
    }

    /**
     * 转换为物料明细实体
     */
    private List<ReceivablesDetailsEntity> convertToMaterialEntities(
            List<ReceivablesImportForm> billItems,
            ReceivablesEntity receivablesEntity) {

        List<ReceivablesDetailsEntity> materials = new ArrayList<>();
        int sortOrder = 0;

        for (ReceivablesImportForm item : billItems) {
            // 跳过没有物料信息的行
            if (StringUtils.isBlank(item.getMaterialCode())) {
                continue;
            }

            ReceivablesDetailsEntity material = new ReceivablesDetailsEntity();
//            material.setOriginBillNo(bi);
            material.setMaterialCode(item.getMaterialCode());
            material.setMaterialName(item.getMaterialName());
            material.setSaleQuantity(item.getSaleQuantity());
            material.setSaleUnit(item.getSaleUnit());

            material.setCreateTime(LocalDateTime.now());
            material.setUpdateTime(LocalDateTime.now());

            materials.add(material);
        }

        return materials;
    }

    /**
     * 日期字符串转换
     */
    private LocalDate convertStringToLocalDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e1) {
            try {
                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            } catch (Exception e2) {
                try {
                    return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
                } catch (Exception e3) {
                    throw new BusinessException("日期格式错误，请使用 yyyy-MM-dd 格式");
                }
            }
        }
    }
}