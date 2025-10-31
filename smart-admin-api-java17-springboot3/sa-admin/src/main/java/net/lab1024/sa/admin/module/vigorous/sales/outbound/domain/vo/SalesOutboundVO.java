package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class SalesOutboundVO {


    @Schema(description = "主键")
    private Long salesBoundId;

    @Schema(description = "单据编号")
    private String billNo;

    @Schema(description = "单据编号")
    private String originBillNo;

    @Schema(description = "出库日期")
    private LocalDate salesBoundDate;

    @Schema(description = "客户编号")
    private Long customerId;

    @Schema(description = "客户编号")
    private String customerCode;

    @Schema(description = "客户名称")
    private String customerName;

    private Integer customerGroup;

    private Integer transferStatus;

    private LocalDate firstOrderDate;
    private LocalDate adjustedFirstOrderDate;

    @Schema(description = "业务员编号")
    private Long salespersonId;

    @Schema(description = "销售员")
    private String salespersonName;

    private Integer salespersonLevelId;
    @Schema(description = "级别提成基准")
    private BigDecimal commissionRate;

    @Schema(description = "金额")
    private BigDecimal amount;

    @Schema(description = "应收比例(%)")
    private Integer rate;

    @Schema(description = "币别")
    private String currencyType;

    @Schema(description = "提成标识")
    private Integer commissionFlag;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
