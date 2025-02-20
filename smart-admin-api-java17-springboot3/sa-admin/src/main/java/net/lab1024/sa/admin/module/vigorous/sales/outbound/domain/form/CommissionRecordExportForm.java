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

    @ExcelProperty("销售出库-单据编号")
    private String salesBillNo;

    @ExcelProperty("销售金额")
    private BigDecimal salesAmount;

    @ExcelProperty("应收单-单据编号")
    private String receiveBillNo;

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("调整后-首单日期")
    private LocalDate adjustedFirstOrderDate;

    @ExcelProperty("首单日期")
    private LocalDate firstOrderDate;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("业务日期")
    private LocalDate orderDate;

    @ExcelProperty("备注")
    private String Remark;

}
