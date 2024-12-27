package net.lab1024.sa.admin.module.vigorous.commission.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 提成规则 分页查询表单
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class CommissionRuleQueryForm extends PageParam {

    @Schema(description = "业务员级别名称")
    private String salespersonLevelName;

    @Schema(description = "币种")
    private String currencuType;

}
