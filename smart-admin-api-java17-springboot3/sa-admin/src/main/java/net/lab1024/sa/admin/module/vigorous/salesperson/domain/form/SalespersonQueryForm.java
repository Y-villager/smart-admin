package net.lab1024.sa.admin.module.vigorous.salesperson.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务员 分页查询表单
 *
 * @Author yxz
 * @Date 2024-12-16 10:56:45
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SalespersonQueryForm extends PageParam {

    @Schema(description = "业务员编码")
    private String salespersonCode;

    @Schema(description = "业务员名称")
    private String salespersonName;

    @Schema(description = "业务员级别")
    private String salespersonLevel;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(hidden = true)
    private Boolean deletedFlag;

}
