package net.lab1024.sa.admin.module.vigorous.customer.domain.vo;

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
public class CustomerExcelVO {

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

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("首单日期")
    private String orderDate;

}
