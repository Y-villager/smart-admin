package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CommissionRecordExportForm {

    @ExcelProperty("销售出库id")
    private Long salesOutboundId;

    @ExcelProperty("销售出库-单据编号")
    private String salesBillNo;

    @ExcelProperty("销售金额")
    private BigDecimal salesAmount;

    @ExcelProperty("应收单-单据编号")
    private String receiveBillNo;

    @ExcelProperty("客户Id")
    private Long customerId;

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("首单日期")
    private LocalDate firstOrderDate;

    @ExcelProperty("销售员Id")
    private Long salespersonId;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("当前销售员提成级别")
    private Long currentSalespersonLevelId;

    @ExcelProperty("上级Id")
    private Long currentParentId;

    @ExcelProperty("上级销售员")
    private String currentParentName;

    @ExcelProperty("上级销售员提成级别")
    private Long currentParentLevelId;

    @ExcelProperty("转交状态")
    private Integer transferStatus;

    @ExcelProperty("业务日期")
    private LocalDate orderDate;

    @ExcelProperty("备注")
    private String Remark;

}
