package net.lab1024.sa.admin.module.vigorous.receivables.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ReceivablesDetailsExportForm {
    @ExcelProperty("单据编号")
    private String originBillNo;

    @ExcelProperty("物料编码")
    private String materialCode;

    @ExcelProperty("物料名称")
    private String materialName;

    @ExcelProperty("序号")
    private String serialNum;

    @ExcelProperty("销售单位")
    private String saleUnit;

    @ExcelProperty("销售数量")
    private Integer saleQuantity;

    private String errorMsg;

}
