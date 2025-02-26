package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 业务提成记录 新建表单
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommissionRecordExcelVO {

    @ExcelProperty("销售出库-单据编号")
    private String salesBillNo;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("首单日期")
    private LocalDate firstOrderDate;

    @ExcelProperty("客户合作年份")
    private Integer customerYear ;

    @ExcelProperty("业务金额")
    private BigDecimal salesAmount;

    @ExcelProperty("应收-币别")
    private String currencyType;

    @ExcelProperty("业务提成系数(%)")
    private BigDecimal businessCommissionRate;

    @ExcelProperty("业务提成金额")
    private BigDecimal businessCommissionAmount;

    @ExcelProperty("管理提成系数(%)")
    private BigDecimal managementCommissionRate;

    @ExcelProperty("业务提成金额")
    private BigDecimal managementCommissionAmount;

    @ExcelProperty("是否为转交客户")
    private Integer transferStatus;

    @ExcelProperty("业务日期")
    private LocalDate orderDate;

}