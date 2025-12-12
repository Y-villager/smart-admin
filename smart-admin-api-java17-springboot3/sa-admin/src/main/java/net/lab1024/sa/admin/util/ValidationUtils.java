package net.lab1024.sa.admin.util;

import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.dto.SalesCommissionDto;
import net.lab1024.sa.admin.module.vigorous.res.ValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ValidationUtils {

    // 验证单据编号（公共方法）
    public static boolean validateBillNo(String billNo, Set<String> existingBillNos,
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
    public static boolean validateCustomer(String customerName, Map<String, ?> customerMap,
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
    public static boolean validateSalesperson(String salespersonName, Map<String, ?> salespersonMap,
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

    // 销售出库-日期检查
    public static void checkOutboundDate(SalesCommissionDto dto, ValidationResult result) {
        if (dto.getOutboundDate() == null) {
            result.addError("缺少销售出库-业务日期");
            return;
        }

        if (LocalDate.of(2025, 1, 1).isAfter(dto.getOrderDate())) {
            // 2025年前销售单
            if (dto.getAdjustedFirstOrderDate() == null) {
                result.addError("2025前业务，但未调整业务首单日期");
            }
        } else {
            // 2025年及以后
            if (dto.getFirstOrderDate() == null) {
                result.addError("客户未设置首单日期");
            }
        }
    }


    // 空值检查
    private void checkNull(String value, ValidationResult result) {
        if (value == null || value.isEmpty()) {
            result.addError("");
        }
    }

    // 转交状态检查
    public static void checkTransferStatus(SalesCommissionDto dto, ValidationResult result) {
        if (dto.getTransferStatus() == null) {
            result.addError("客户未设置转交状态");
            return;
        }

//        Long customerSalesperson = dto.getCustomerSalespersonId();
//        if (customerSalesperson == null || !customerSalesperson.equals(dto.getSalespersonId())) {
//            // 与负责人ID不符，提成设置为转交
//            dto.setTransferStatus(SystemYesNo.YES.getValue());
//
//            // 检查是否上下级关系
//            boolean isSuperior = customerSalesperson != null &&
//                    customerSalesperson.equals(dto.getPSalespersonId());
//            boolean isSubordinate = isSubordinate(customerSalesperson, dto.getSalespersonId());
//
//            if (!isSuperior && !isSubordinate) {
//                result.addRemind("客户负责人与出库业务员不同，且非上下级关系，"
//                        + "客户可能已转交，需联系管理员修改客户信息");
//            }
//        }
    }


    // 报关信息检查
    public static void checkCustomsDeclaration(SalesCommissionDto dto, ValidationResult result) {
        if (dto.getIsCustomsDeclaration() == null) {
            result.addError("客户未设置报关信息");
        }
    }

    // 提成记录检查
    public static void checkCommissionFlag(SalesCommissionDto dto, ValidationResult result) {
        if (dto.getCommissionFlag() != null && dto.getCommissionFlag() == 1) {
            result.addError("已生成提成记录，请勿重复生成");
        }
    }


    // 客户业务员信息检查
    public static void checkCustomerSalesperson(SalesCommissionDto dto, ValidationResult result) {
        if (dto.getCustomerSalespersonId() == null) {
            result.addError("客户数据中缺少业务员信息");
        }
    }

}