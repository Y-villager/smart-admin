package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 业务提成记录 更新表单
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Data
public class CommissionRecordUpdateForm {

    @Schema(description = "提成id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "提成id 不能为空")
    private Long commissionId;

    @Schema(description = "提成类型(0业务1管理）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "提成类型(0业务1管理） 不能为空")
    private Integer commissionType;

    @Schema(description = "提成金额")
    private BigDecimal amount;

}