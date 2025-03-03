package net.lab1024.sa.admin.module.vigorous.commission.rule.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.admin.enumeration.CommissionTypeEnum;
import net.lab1024.sa.admin.enumeration.TransferStatusEnum;
import net.lab1024.sa.base.common.domain.PageParam;
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

    @Schema(description = "客户是否报关(0客户未报关 1报关)")
    private Integer isCustomsDeclaration;

    @SchemaEnum(value = TransferStatusEnum.class, desc = "转交状态（0自主开发，非0转交）")
    @CheckEnum(value = TransferStatusEnum.class, message = "转交状态（0自主开发，非0转交） 错误")
    private Integer transferStatus;

    @SchemaEnum(value = CommissionTypeEnum.class, desc = "提成类型（1业务 2管理）")
    @CheckEnum(value = CommissionTypeEnum.class, message = "提成类型（1业务 2管理） 错误")
    private Integer commissionType;

}
