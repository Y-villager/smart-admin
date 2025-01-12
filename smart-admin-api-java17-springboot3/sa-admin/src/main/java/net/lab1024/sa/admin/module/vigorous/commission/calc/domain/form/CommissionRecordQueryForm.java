package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务提成记录 分页查询表单
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class CommissionRecordQueryForm extends PageParam {

    @Schema(description = "业务员")
    private String salespersonName;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "提成类型")
    private String commssionType;

    @Schema(description = "销售出库日期")
    private LocalDate orderDateBegin;

    @Schema(description = "销售出库日期")
    private LocalDate orderDateEnd;

}
