package net.lab1024.sa.admin.module.vigorous.salesperson.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 业务员 更新表单
 *
 * @Author yxz
 * @Date 2024-12-16 10:56:45
 * @Copyright (c)2024 yxz
 */

@Data
public class SalespersonUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long id;

    @Schema(description = "业务员编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员编码 不能为空")
    private String salespersonCode;

    @Schema(description = "业务员名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员名称 不能为空")
    private String salespersonName;

    @Schema(description = "部门编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "部门编码 不能为空")
    private Long departmentId;

    @Schema(description = "级别编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "级别编码 不能为空")
    private Integer salespersonLevelId;

    @Schema(description = "上级id")
    private Long parentId;

}