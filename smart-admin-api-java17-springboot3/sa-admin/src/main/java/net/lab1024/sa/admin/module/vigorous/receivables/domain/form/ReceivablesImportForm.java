package net.lab1024.sa.admin.module.vigorous.receivables.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 应收单 新建表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Data
public class ReceivablesImportForm {

    @ExcelProperty("单据编号")
    private String billNo;

    @ExcelProperty("源单编号")
    private String originBillNo;

    @ExcelProperty("业务日期")
    private String receivablesDate;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("客户")
    private String customerName;

    @ExcelProperty("价税合计")
    private BigDecimal amount;

    @ExcelProperty("币别")
    private String currencyType;

    @ExcelProperty("应收比例(%)")
    private Integer rate;

    @ExcelProperty("错误信息")
    private String errorMsg;

}