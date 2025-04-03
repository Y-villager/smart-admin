package net.lab1024.sa.admin.module.vigorous.receivables.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank(message = "单据编号不能为空")
    @ExcelProperty("单据编号")
    private String billNo;

    @ExcelProperty("源单编号")
    private String originBillNo;

    @ExcelProperty("业务日期")
    @NotBlank(message = "业务日期不能为空")
    private String receivablesDate;

    @ExcelProperty("销售员")
    @NotBlank(message = "销售员不能为空")
    private String salespersonName;

    @ExcelProperty("客户")
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @ExcelProperty("价税合计")
    private BigDecimal amount;

    @ExcelProperty("币别")
    @NotBlank(message = "币别不能为空")
    private String currencyType;

    @ExcelProperty("汇率")
    private BigDecimal exchangeRate;

    @ExcelProperty("价税合计本位币")
    private BigDecimal fallAmount;

    @ExcelProperty("付款方")
    private String payer;

    @ExcelProperty("应收比例(%)")
    private Integer rate;

    @ExcelProperty("错误信息")
    private String errorMsg;

}