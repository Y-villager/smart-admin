package net.lab1024.sa.admin.module.vigorous.customer.domain.form;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import net.lab1024.sa.admin.convert.CustomerGroupEnumConverter;
import net.lab1024.sa.admin.convert.FlexibleDateConverter;
import net.lab1024.sa.admin.convert.TransferStatusEnumConverter;

import java.time.LocalDate;

/**
 * 顾客 导入表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Data
public class CustomerImportForm {

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty(value = "客户名称")
    private String customerName;

    @ExcelProperty("简称")
    private String shortName;

    @ExcelProperty("国家")
    private String country;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty(value = "客户分组", converter = CustomerGroupEnumConverter.class)
    private Integer customerGroup;

    @ExcelProperty("结算币别")
    private String currencyType;

    @ExcelProperty(value = "首单日期",  converter = FlexibleDateConverter.class)
    private LocalDate orderDate;

    @ExcelProperty(value = "转交状态", converter = TransferStatusEnumConverter.class)
    private Integer transferStatus;

    @ExcelProperty(value = "创建日期",  converter = FlexibleDateConverter.class)
    private LocalDate createDate;

    @ExcelIgnore
    private String errorMsg;

}