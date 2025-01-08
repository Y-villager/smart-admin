package net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 业务员级别变动记录 分页查询表单
 *
 * @Author yxz
 * @Date 2025-01-07 08:58:41
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SalespersonLevelRecordQueryForm extends PageParam {

    @Schema(description = "业务员名称")
    private String salespersonName;

    @Schema(description = "变动时间")
    private LocalDate createTimeBegin;

    @Schema(description = "变动时间")
    private LocalDate createTimeEnd;

}
