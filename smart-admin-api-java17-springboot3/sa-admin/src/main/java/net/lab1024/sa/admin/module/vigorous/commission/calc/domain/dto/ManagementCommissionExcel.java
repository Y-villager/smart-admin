package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class ManagementCommissionExcel extends CommissionExcel {

    @ExcelProperty(value = "管理者id", index = 9)
    private Long currentParentId;

    @ExcelProperty(value = "管理者名称", index = 10)
    private String currentParentName;

    @ExcelProperty(value = "管理提成系数(%)", index = 11)
    private BigDecimal managementCommissionRate;

    @ExcelProperty(value = "管理提成金额", index = 12)
    private BigDecimal managementCommissionAmount;

}
