package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售出库 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalesOutboundVO {


    @Schema(description = "主键")
    private Long salesBoundId;

    @Schema(description = "单据编号")
    private String billNo;

    @Schema(description = "出库日期")
    private LocalDate salesBoundDate;

    @Schema(description = "客户编码")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "业务员编号")
    private Long salespersonId;

    @Schema(description = "销售员")
    private String salespersonName;

    @Schema(description = "金额")
    private BigDecimal amount;

}