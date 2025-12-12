package net.lab1024.sa.admin.module.vigorous.sales.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderExcludeForm {
    @Schema(description = "顾客")
    private String customerName;

    @Schema(description = "假删除业务员")
    private Integer deletedSalesmanFlag;

    @Schema(description = "禁用状态业务员")
    private Integer disabledSalesmanFlag;
}
