package net.lab1024.sa.admin.module.vigorous.salesperson.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务员 分页查询表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:42:49
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SalespersonQueryForm extends PageParam {

    @Schema(description = "业务员名称")
    private String salesperson_name;

    @Schema(description = "业务员编码")
    private String salesperson_code;

}
