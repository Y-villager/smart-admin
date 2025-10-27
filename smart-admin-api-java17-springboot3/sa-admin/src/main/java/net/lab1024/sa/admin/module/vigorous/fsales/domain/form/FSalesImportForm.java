package net.lab1024.sa.admin.module.vigorous.fsales.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 应收单 新建表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Data
public class FSalesImportForm {

    @NotBlank(message = "单据编号不能为空")
    @ExcelProperty("单据编号")
    private String billNo;

    @NotBlank(message = "日期不能为空")
    @ExcelProperty("日期")
    @DateTimeFormat("yyyy-MM-dd")
    private LocalDate billDate;

    @ExcelProperty("源单编号")
    private String originBillNo;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("客户")
    @NotBlank(message = "客户名称不能为空")
    private String customerName;

    @ExcelProperty("错误信息")
    private String errorMsg;

}