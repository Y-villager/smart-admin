package net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 业务员级别 新建表单
 *
 * @Author yxz
 * @Date 2024-12-14 16:07:14
 * @Copyright (c)2024 yxz
 */

@Data
public class SalespersonLevelAddForm {

    @Schema(description = "业务员级别名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员级别名称 不能为空")
    private String salespersonLevelName;

    @Schema(description = "提成比例", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "提成比例 不能为空")
    private BigDecimal commissionRate;

}