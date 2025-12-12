package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售-应收 类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesCommissionDto {

    private Long salesOutboundId;

    @Schema(description = "销售订单-单据编号")
    private String salesOrderBillNo;

    @Schema(description = "销售订单-单据日期")
    private LocalDate orderDate;

    @Schema(description = "发货通知单-单据编号")
    private String fSalesBillNo;

    @Schema(description = "销售出库-单据编号")
    private String salesBillNo;

    @Schema(description = "销售出库-单据编号")
    private LocalDate outboundDate;

    @Schema(description = "销售金额")
    private BigDecimal salesAmount;

    @Schema(description = "应收币别")
    private String currencyType;

    @Schema(description = "应收单-单据编号")
    private String receiveBillNo;

    private String receiveRate;
    private BigDecimal exchangeRate;
    private BigDecimal fallAmount;

    @Schema(description = "客户id")
    private Long customerId;
    private String customerCode;
    private String customerName;
    private LocalDate customerCreateDate;
    private Long customerSalespersonId;

    @Schema(description = "客户是否转交/转交状态")
    private Integer transferStatus;

    @Schema(description = "客户是否报关")
    private Integer isCustomsDeclaration;

    @Schema(description = "调整后-首单日期")
    private LocalDate adjustedFirstOrderDate;
    private LocalDate firstOrderDate;

    @Schema(description = "业务员id")
    private Long salespersonId;
    private String salespersonName;

    @Schema(description = "当时业务员级别id")
    private String salespersonLevelName;
    private BigDecimal levelRate;

    @Schema(description = "当时上级id")
    private Long pSalespersonId;
    private String pSalespersonName;
    private String pSalespersonLevelName;
    private BigDecimal pLevelRate;

    @Schema(description = "提成标识")
    private Integer commissionFlag;

    private String errMsg;
    private String remindMsg;

}
