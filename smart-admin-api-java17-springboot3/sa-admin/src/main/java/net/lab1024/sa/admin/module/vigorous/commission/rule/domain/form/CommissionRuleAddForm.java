package net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提成规则 新建表单
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@Data
public class CommissionRuleAddForm {

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