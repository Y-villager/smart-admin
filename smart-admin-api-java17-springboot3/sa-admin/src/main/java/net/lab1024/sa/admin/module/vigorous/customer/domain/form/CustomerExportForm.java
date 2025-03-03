package net.lab1024.sa.admin.module.vigorous.customer.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 顾客 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */
@Builder
@Data
public class CustomerExportForm {

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("简称")
    private String shortName;

    @ExcelProperty("国家")
    private String country;

    @ExcelProperty("客户分组")
    private String customerGroup;

    @ExcelProperty("结算币别")
    private String currencyType;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("首单日期")
    private String orderDate;

    @ExcelProperty("转交状态")
    private String transferStatus;

    @ExcelProperty("是否报关")
    private Integer isCustomsDeclaration;

    @ExcelProperty(value = "错误信息")
    private String errorMsg;
}
