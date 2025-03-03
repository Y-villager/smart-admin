package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
@Data
public class SalesCommissionExportForm {

    @ExcelProperty("出库单日期")
    private LocalDate orderDate;

    @ExcelProperty("销售出库-单据编号")
    private String salesBillNo;

    @ExcelProperty("应收单-单据编号")
    private String receiveBillNo;

    @ExcelProperty("销售金额")
    private BigDecimal salesAmount;

    @ExcelProperty("应收-币别")
    private String currencyType;

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("初始-首单日期")
    private LocalDate firstOrderDate;

    @ExcelProperty("计算时-首单日期")
    private LocalDate adjustedFirstOrderDate;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("上级销售员")
    private String currentParentName;

    @ExcelProperty("销售员级别系数")
    private BigDecimal levelRate;

    @ExcelProperty("转交状态")
    private Integer transferStatus;

    @ExcelProperty("客户是否报关")
    private Integer isDeclared;

    @ExcelProperty("错误信息")
    private String errMsg;

}
