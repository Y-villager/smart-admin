package net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;
import net.lab1024.sa.base.common.json.deserializer.DictValueVoDeserializer;

/**
 * 提成规则 更新表单
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@Data
public class CommissionRuleUpdateForm {

    @Schema(description = "提成规则id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "提成规则id 不能为空")
    private Long ruleId;

    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "币种 不能为空")
    @JsonDeserialize(using = DictValueVoDeserializer.class)
    private String currencyType;

    @Schema(description = "业务员级别id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "业务员级别id 不能为空")
    private Integer salespersonLevelId;

    @Schema(description = "首单比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "首单比例 不能为空")
    private BigDecimal firstOrderRate;

    @Schema(description = "基础比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "基础比例 不能为空")
    private BigDecimal baseRate;

    @Schema(description = "备注")
    private String remark;

}