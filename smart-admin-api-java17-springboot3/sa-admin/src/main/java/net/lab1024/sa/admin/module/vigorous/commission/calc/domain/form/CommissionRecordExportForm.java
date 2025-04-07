package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form;

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
public class CommissionRecordExportForm {

    @ExcelProperty("业务日期")
    private LocalDate orderDate;

    @ExcelProperty("销售出库-单据编号")
    private String salesBillNo;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("销售员级别系数")
    private BigDecimal salespersonLevelRate;

    @ExcelProperty("销售员上级")
    private String currentParentName;

    @ExcelProperty("上级系数")
    private BigDecimal currentParentLevelRate;

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("初始-首单日期")
    private LocalDate firstOrderDate;

    @ExcelProperty("调整后-首单日期")
    private LocalDate adjustedFirstOrderDate;

    @ExcelProperty("客户合作年份")
    private Integer customerYear ;

    @ExcelProperty("客户年份系数")
    private BigDecimal customerYearRate ;

    @ExcelProperty("提成类别")
    private Integer commissionType;

    @ExcelProperty("税收合计")
    private BigDecimal salesAmount;

    @ExcelProperty("应收-币别")
    private String currencyType;

    @ExcelProperty("汇率")
    private BigDecimal exchangeRate;

    @ExcelProperty("税收合计本位币")
    private BigDecimal fallAmount;

    @ExcelProperty("是否为转交客户")
    private String isTransfer;

    @ExcelProperty("是否报关")
    private String isCustomsDeclaration;

    @ExcelProperty("最终提成系数(%)")
    private BigDecimal commissionRate;

    @ExcelProperty("提成金额(人民币)")
    private BigDecimal commissionAmount;


}