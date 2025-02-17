package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesOutboundExcludeForm {
    @Schema(description = "顾客们")
    private String customerName;

    @Schema(description = "提成标识")
    private Integer commissionFlag;
}
