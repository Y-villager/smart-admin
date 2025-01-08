package

        net.lab1024.sa.admin.module.vigorous.commission.rule.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.json.serializer.DictValueVoSerializer;

import java.math.BigDecimal;

/**
 * 提成规则 列表VO
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@Data
public class CommissionRuleVO {


    @Schema(description = "提成规则id")
    private Long ruleId;

    @Schema(description = "币种")
    @JsonSerialize(using = DictValueVoSerializer.class)
    private String currencyType;

    @Schema(description = "业务员级别id")
    private Integer salespersonLevelId;

    private String salespersonLevelName;

    @Schema(description = "首单比例")
    private BigDecimal firstOrderRate;

    @Schema(description = "基础比例")
    private BigDecimal baseRate;

    @Schema(description = "备注")
    private String remark;

}
