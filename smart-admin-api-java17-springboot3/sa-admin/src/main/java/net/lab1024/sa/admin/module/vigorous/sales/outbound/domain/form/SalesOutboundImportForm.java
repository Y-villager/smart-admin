package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 销售出库 新建表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@Data
public class SalesOutboundImportForm {

    @ExcelProperty("单据编号")
    private String billNo;

    @ExcelProperty("源单编号")
    private String originBillNo;

    @ExcelProperty(value = "日期")
    private String salesBoundDate;

    @ExcelProperty("客户")
    private String customerName;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("金额")
    private BigDecimal amount;

    @ExcelProperty("报错信息")
    private String errorMsg;

}