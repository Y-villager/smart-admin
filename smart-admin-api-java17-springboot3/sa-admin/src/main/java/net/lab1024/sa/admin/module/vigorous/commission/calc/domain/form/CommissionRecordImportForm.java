package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 业务提成记录 新建表单
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Data
public class CommissionRecordImportForm {

    @ExcelProperty("销售出库-单据编号")
    private String salesBillNo;

    @ExcelProperty("业务金额")
    private String salesAmount;

    @ExcelProperty("币别")
    private String currencyType;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("首单日期")
    private Integer firstOrderDate;

    @ExcelProperty("客户合作年份")
    private Integer customerYear;

    @ExcelProperty("业务提成系数(%)")
    private BigDecimal businessCommissionRate;

    @ExcelProperty("业务提成金额")
    private BigDecimal businessCommissionAmount;

    @ExcelProperty("管理提成系数(%)")
    private BigDecimal managementCommissionRate;

    @ExcelProperty("业务提成金额")
    private BigDecimal managementCommissionAmount;

    @ExcelProperty("是否为转交客户")
    private Integer isTransfer;

    @ExcelProperty("业务日期")
    private LocalDate orderDate;

}