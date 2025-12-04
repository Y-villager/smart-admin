package net.lab1024.sa.admin.util;

import net.lab1024.sa.admin.module.vigorous.res.ValidationResult;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
            errorMessages.add("【追加】单据编号已存在;" );
            return false;
        }
        if (!addMode && !existingBillNos.contains(billNo)) {
            errorMessages.add("【覆盖】不存在单据;");
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
            errorMessages.add("客户不存在;" );
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
            errorMessages.add("销售员不存在;" );
            return false;
        }
        return true;
    }

    /**
     * 校验销售金额 * 汇率 ≈ 税收合计本位币
     * @param salesAmount 销售金额
     * @param exchangeRate 汇率
     * @param fallAmount 税收合计本位币
     * @param scale 小数精度（默认为2）
     * @return 校验结果
     */
    public static ValidationResult validateAmount(BigDecimal salesAmount,
                                                  BigDecimal exchangeRate,
                                                  BigDecimal fallAmount,
                                                  int scale) {
        ValidationResult result = new ValidationResult();
        BigDecimal ALLOWED_ERROR = new BigDecimal("0.05");

        // 1. 基础非空校验
        if (salesAmount == null && fallAmount != null && BigDecimal.ZERO.compareTo(fallAmount) != 0 ) {
            result.addError("销售金额不能为空");
            return result; // 立即返回，无需继续检查
        }else if (salesAmount == null) {
            salesAmount = BigDecimal.ZERO;
        }

        if (exchangeRate == null) {
            result.addError("汇率不能为空");
        }
        // salesAmount是0，fallAmount需要调整为0
        if (salesAmount.compareTo(BigDecimal.ZERO) == 0 && fallAmount == null) {
            fallAmount = BigDecimal.ZERO; // 自动调整为0
        }
        if (fallAmount == null) {
            result.addError("税收合计本位币不能为空");
            return result;
        }

        // 2. 计算期望值：销售金额 * 汇率
        BigDecimal expectedAmount = fallAmount.divide(exchangeRate, scale, RoundingMode.HALF_UP);

        // 3. 实际值四舍五入到相同精度
        BigDecimal actualAmount = salesAmount.setScale(scale, RoundingMode.HALF_UP);

        // 4. 计算绝对误差
        BigDecimal difference = expectedAmount.subtract(actualAmount).abs();

        // 5. 校验误差是否在允许范围内
        if (difference.compareTo(ALLOWED_ERROR) > 0) {
            result.addError(String.format(
                    "金额、本位币误差为%s，超过(%s)，请检查数据",
                    difference.setScale(4, RoundingMode.HALF_UP),
                    ALLOWED_ERROR
            ));
        }

        return result;
    }
}