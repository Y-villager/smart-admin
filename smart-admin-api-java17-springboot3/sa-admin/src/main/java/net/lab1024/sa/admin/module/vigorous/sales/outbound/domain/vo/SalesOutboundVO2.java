package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lab1024.sa.admin.module.vigorous.customer.domain.vo.CustomerVO;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesEntity;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo.SalespersonVO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class SalesOutboundVO2 {


    @Schema(description = "主键")
    private Long salesBoundId;

    @Schema(description = "单据编号")
    private String billNo;

    @Schema(description = "出库日期")
    private LocalDate salesBoundDate;

    @Schema(description = "客户")
    private CustomerVO customer;

    @Schema(description = "销售员")
    private SalespersonVO salesperson;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "应收单")
    private ReceivablesEntity receivables;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
