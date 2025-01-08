package net.lab1024.sa.admin.module.vigorous.firstorder.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
@Data

public class FirstOrderImportForm {

    private String errorMsg;

    @ExcelProperty("客户")
    private String customerCode;

    @ExcelProperty("客户")
    private String customerName;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("单据编号")
    private String billNo;

    @ExcelProperty("首单日期")
    private String orderDate;



}