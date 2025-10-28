package net.lab1024.sa.admin.util;

import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;
import java.util.*;

@Component
public class ValidationUtils {

    // 验证单据编号（公共方法）
    public boolean validateBillNo(String billNo, Set<String> existingBillNos,
                                  boolean addMode, List<String> errorMessages) {
        if (StringUtils.isBlank(billNo)) {
            errorMessages.add("单据编号不能为空");
            return false;
        }
        if (addMode && existingBillNos.contains(billNo)) {
            errorMessages.add("【追加】单据编号已存在: " + billNo);
            return false;
        }
        if (!addMode && !existingBillNos.contains(billNo)) {
            errorMessages.add("【覆盖】不存在单据: " + billNo);
            return false;
        }
        if (billNo.length() > 50) {
            errorMessages.add("单据编号长度不能超过50个字符");
            return false;
        }
        return true;
    }

    // 验证客户（公共方法）
    public boolean validateCustomer(String customerName, Map<String, ?> customerMap,
                                    List<String> errorMessages) {
        if (StringUtils.isBlank(customerName)) {
            errorMessages.add("客户名称不能为空");
            return false;
        }
        if (!customerMap.containsKey(customerName)) {
            errorMessages.add("客户不存在: " + customerName);
            return false;
        }
        return true;
    }

    // 验证销售员（公共方法）
    public boolean validateSalesperson(String salespersonName, Map<String, ?> salespersonMap,
                                       List<String> errorMessages) {
        if (StringUtils.isBlank(salespersonName)) {
            errorMessages.add("销售员名称不能为空");
            return false;
        }
        if (!salespersonMap.containsKey(salespersonName)) {
            errorMessages.add("销售员不存在: " + salespersonName);
            return false;
        }
        return true;
    }
}