package net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 业务员级别变动记录 更新表单
 *
 * @Author yxz
 * @Date 2025-01-07 08:58:41
 * @Copyright (c)2024 yxz
 */

@Data
public class SalespersonLevelRecordUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Integer id;

    @Schema(description = "业务员id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "业务员id 不能为空")
    private Long salespersonId;

    @Schema(description = "先前级别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "先前级别 不能为空")
    private Integer oldLevel;

    @Schema(description = "现在级别", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "现在级别 不能为空")
    private Integer newLevel;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间 不能为空")
    private LocalDate startDate;

    @Schema(description = "结束时间")
    private LocalDate endDate;

    @Schema(description = "变动原因", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "变动原因 不能为空")
    private String changeReason;


}