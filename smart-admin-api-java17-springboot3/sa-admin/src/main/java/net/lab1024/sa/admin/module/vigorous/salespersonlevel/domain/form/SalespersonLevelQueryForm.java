package net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务员级别 分页查询表单
 *
 * @Author yxz
 * @Date 2024-12-14 16:07:14
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SalespersonLevelQueryForm extends PageParam {

    @Schema(description = "业务员级别名称")
    private String levelName;

}
