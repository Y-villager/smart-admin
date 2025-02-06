package

        net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提成规则 列表VO
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommissionRuleVO {


    @Schema(description = "提成规则id")
    private Long ruleId;

    @Schema(description = "转交状态（0自主开发，非0转交）")
    private Integer transferStatus;

    @Schema(description = "客户分组(1内贸 2外贸)")
    private Integer customerGroup;

    @Schema(description = "提成类型（1业务 2管理）")
    private Integer commissionType;

    @Schema(description = "是否计算公式（0否 1是）")
    private Integer useDynamicFormula;

    @Schema(description = "提成系数")
    private BigDecimal commissionRate;

    @Schema(description = "计算公式id")
    private Long formulaId;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建")
    private LocalDateTime createTime;

    @Schema(description = "更新")
    private LocalDateTime updateTime;


}
