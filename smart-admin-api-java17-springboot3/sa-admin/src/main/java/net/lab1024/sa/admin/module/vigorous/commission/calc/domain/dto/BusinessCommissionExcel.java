package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessCommissionExcel extends CommissionExcel {

    @ExcelProperty(value = "销售员id", index = 9)
    private Long salespersonId;

    @ExcelProperty(value = "销售员名称", index = 10)
    private String salespersonName;

    @ExcelProperty(value = "业务提成系数(%)", index = 11)
    private BigDecimal businessCommissionRate;

    @ExcelProperty(value = "业务提成金额", index = 12)
    private BigDecimal businessCommissionAmount;


}
