package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
public class CommissionRecordExportWithMaterialForm {

    @ExcelProperty("提成类别")
    private String commissionType;

    @ExcelProperty("销售出库")
    @ColumnWidth(12)
    private String salesBillNo;

    @ExcelProperty("出库日期")
    @ColumnWidth(12)
    private LocalDate outboundDate;

    @ExcelProperty("销售订单")
    @ColumnWidth(12)
    private String salesOrderBillNo;

    @ExcelProperty("订单类型")
    @ColumnWidth(16)
    private String orderType;

//    @ExcelProperty("销售出库-单据编号")
//    @ColumnWidth(12)
//    private String salesBillNo;

    @ExcelProperty("下游应收单")
    @ColumnWidth(12)
    private String receivablesNo;

    @ExcelProperty("明细.序号")
    private Integer serialNum;
    @ExcelProperty("物料名称")
    private String materialName;
    @ExcelProperty("销售数量")
    private Integer salesQuantity;
    @ExcelProperty("销售单位")
    private String saleUnit;
    @ExcelProperty("销售金额")
    private BigDecimal materialPrice;

    @ExcelProperty("销售员id")
    @ExcelIgnore
    private Long salespersonId;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("提成级别")
    private String salespersonLevelName;

    @ExcelProperty("级别提成系数(%)")
    private BigDecimal salespersonLevelRate;

    @ExcelProperty("销售员上级id")
    @ExcelIgnore
    private Long currentParentId;

    @ExcelProperty("销售员上级")
    private String currentParentName;

    @ExcelProperty("上级提成级别")
    private String currentParentLevelName;

    @ExcelProperty("上级级别系数(%)")
    private BigDecimal currentParentLevelRate;

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty("初始-首单日期")
    private LocalDate firstOrderDate;

    @ExcelProperty("调整后-首单日期")
    private LocalDate adjustedFirstOrderDate;

    @ExcelProperty("客户年份系数")
    private BigDecimal customerYearRate ;

    @ExcelProperty("应收金额")
    private BigDecimal salesAmount;

    @ExcelProperty("应收-币别")
    private String currencyType;

    @ExcelProperty("汇率")
    private BigDecimal exchangeRate;

    @ExcelProperty("应收金额本位币")
    private BigDecimal fallAmount;

    @ExcelProperty("是否为转交客户")
    private String isTransfer;

    @ExcelProperty("是否报关")
    private String isCustomsDeclaration;

    @ExcelProperty("最终提成系数(%)")
    private BigDecimal commissionRate;

    @ExcelProperty("提成金额(人民币)")
    private BigDecimal commissionAmount;

    @ExcelIgnore
    private List<ReceivablesDetailsEntity> materialItems;

}