package net.lab1024.sa.admin.module.vigorous.receivables.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 应收单 分页查询表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceivablesQueryForm extends PageParam {
    @Schema(description = "单据编号")
    private String billNo;

    @Schema(description = "源单编号")
    private String originBillNo;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "销售员id")
    private Integer salespersonId;

    @Schema(description = "币种")
    private String currencyType;

    @Schema(description = "收款日期")
    private LocalDate receivablesDateBegin;

    @Schema(description = "收款日期")
    private LocalDate receivablesDateEnd;

    @Schema(description = "是否有明细")
    private Boolean hasMaterial;

}
