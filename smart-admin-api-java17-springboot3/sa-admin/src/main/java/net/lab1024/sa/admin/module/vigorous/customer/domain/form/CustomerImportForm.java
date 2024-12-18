package net.lab1024.sa.admin.module.vigorous.customer.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 顾客 导入表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Data
public class CustomerImportForm {
    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("简称")
    private String shortName;

    @ExcelProperty("国家")
    private String country;

    @ExcelProperty("客户分组")
    private String customerGroup;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("客户编码")
    private String customerCode;

    private String errorMsg;

}