package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesCommissionDto {

    private Long SalesOutboundId;

    @Schema(description = "销售出库-单据编号")
    private String salesBillNo;

    @Schema(description = "销售出库日期")
    private LocalDate salesBoundDate;

    @Schema(description = "销售金额")
    private BigDecimal salesAmount;

    @Schema(description = "销售出库-单据编号")
    private String receiveBillNo;

    private String receiveRate;

    private Long customerId;
    private Integer customerGroup;
    private Integer transferStatus;

    @Schema(description = "调整后-首单日期")
    private LocalDate adjustedFirstOrderDate;
    private LocalDate firstOrderDate;

    private Long salespersonId;
    private Integer salespersonLevelId;
    private BigDecimal levelRate;

    private Long pSalespersonId;
    private Integer pSalespersonLevelId;
    private BigDecimal pLevelRate;

    private Integer commissionFlag;


}
