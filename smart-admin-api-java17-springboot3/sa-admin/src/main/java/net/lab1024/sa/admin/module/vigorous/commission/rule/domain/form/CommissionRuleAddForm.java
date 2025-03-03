package net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 提成规则 新建表单
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@Data
public class CommissionRuleAddForm {

    @Schema(description = "转交状态（0自主开发，非0转交）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "转交状态（0自主开发，非0转交） 不能为空")
    private Integer transferStatus;

    private Integer isCustomsDeclaration;

    @Schema(description = "提成类型（1业务 2管理）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "提成类型（1业务 2管理） 不能为空")
    private Integer commissionType;

    @Schema(description = "是否计算公式（0否 1是）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否计算公式（0否 1是） 不能为空")
    private Integer useDynamicFormula;

    @Schema(description = "提成系数", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "提成系数 不能为空")
    private BigDecimal commissionRate;

    @Schema(description = "计算公式id")
    private Long formulaId;

    @Schema(description = "备注")
    private String remark;

}