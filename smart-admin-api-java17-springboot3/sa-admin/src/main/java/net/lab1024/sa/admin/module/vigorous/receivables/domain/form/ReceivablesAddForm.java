package net.lab1024.sa.admin.module.vigorous.receivables.domain.form;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.json.deserializer.DictValueVoDeserializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 应收单 新建表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:46:31
 * @Copyright yxz
 */

@Data
public class ReceivablesAddForm {

    @Schema(description = "应收表-单据编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "应收表-单据编号 不能为空")
    private String billNo;

    @Schema(description = "应收日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "应收日期 不能为空")
    private LocalDate receivablesDate;

    @Schema(description = "业务员编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员编号 不能为空")
    private String salespersonId;

    @Schema(description = "客户编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户编号 不能为空")
    private String customerId;

    @Schema(description = "单据类型")
    private String receivablesType;

    @Schema(description = "税收合计")
    private BigDecimal amount;

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "币种 不能为空")
    @JsonDeserialize(using = DictValueVoDeserializer.class)
    private String currencyType;

    @Schema(description = "应收比例(%)")
    private Integer rate;

}