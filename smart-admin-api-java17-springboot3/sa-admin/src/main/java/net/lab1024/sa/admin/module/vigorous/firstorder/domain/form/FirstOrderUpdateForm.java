package net.lab1024.sa.admin.module.vigorous.firstorder.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 客户首单信息 更新表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@Data
public class FirstOrderUpdateForm {

    @Schema(description = "首单信息编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "首单信息编号 不能为空")
    private Long firstOrderId;

    @Schema(description = "客户编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户编码 不能为空")
    private String customerId;

    @Schema(description = "业务员编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员编码 不能为空")
    private String salespersonId;

    @Schema(description = "首单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "首单编号 不能为空")
    private String billNo;

}