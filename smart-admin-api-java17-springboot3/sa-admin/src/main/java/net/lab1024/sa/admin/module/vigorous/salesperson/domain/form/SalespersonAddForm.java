package net.lab1024.sa.admin.module.vigorous.salesperson.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 业务员 新建表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:42:49
 * @Copyright (c)2024 yxz
 */

@Data
public class SalespersonAddForm {

    @Schema(description = "业务员编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员编码 不能为空")
    private String salespersonCode;

    @Schema(description = "业务员名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员名称 不能为空")
    private String salespersonName;

    @Schema(description = "职位", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "职位 不能为空")
    private String position;

}