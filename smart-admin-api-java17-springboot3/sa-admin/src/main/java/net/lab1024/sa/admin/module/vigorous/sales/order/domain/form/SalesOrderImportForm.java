package net.lab1024.sa.admin.module.vigorous.sales.order.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SalesOrderImportForm {

    @ExcelProperty("单据编号")
    private String billNo;

    @ExcelProperty("日期")
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate orderDate;

    @ExcelProperty("客户")
    private String customerName;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("单据类型")
    private String orderType;

    @ExcelProperty("价税合计")
    private BigDecimal amount;

    @ExcelProperty("汇率")
    private BigDecimal exchangeRate;

    @ExcelProperty("价税合计（本位币）")
    private BigDecimal fallAmount;

    @ExcelProperty("错误信息")
    private String errMsg;

}
