package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CommissionExcel {

    @ExcelProperty(value = "销售出库-单据编号", index = 0)
    private String salesBillNo;

    @ExcelProperty(value = "出库日期", index = 1)
    private LocalDate orderDate;

    @ExcelProperty(value = "销售金额", index = 2)
    private BigDecimal salesAmount;

    @ExcelProperty(value = "应收单币别", index = 3)
    private String currencyType;

    @ExcelProperty(value = "客户编码", index = 4)
    private String customerCode;

    @ExcelProperty(value = "客户名称", index = 5)
    private String customerName;

    @ExcelProperty(value = "客户首单日期", index = 6)
    private LocalDate firstOrderDate;

    @ExcelProperty(value = "合作年数", index = 7)
    private Integer customerYear;

    @ExcelProperty(value = "转交状态", index = 8)
    private Boolean isTransfer;
}

