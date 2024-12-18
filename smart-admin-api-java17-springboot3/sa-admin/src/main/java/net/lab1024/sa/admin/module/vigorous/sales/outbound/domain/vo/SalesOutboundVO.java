package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

/**
 * 销售出库 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@Data
@Builder
public class SalesOutboundVO {


    @Schema(description = "主键")
    private Long salesBoundId;

    @Schema(description = "单据编号")
    private String billNo;

    @Schema(description = "出库日期")
    private LocalDate salesBoundDate;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "业务员编码")
    private String salespersonCode;

    @Schema(description = "金额")
    private BigDecimal amount;

}
