package net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form;

import net.lab1024.sa.admin.enumeration.CustomerGroupEnum;
import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.swagger.SchemaEnum;
import net.lab1024.sa.base.common.validator.enumeration.CheckEnum;

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

    @SchemaEnum(value = CustomerGroupEnum.class, desc = "客户分组(1内贸 2外贸)")
    @CheckEnum(value = CustomerGroupEnum.class, message = "客户分组(1内贸 2外贸) 错误")
    private Integer customerGroup;

}
