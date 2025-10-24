package

        net.lab1024.sa.admin.module.vigorous.sales.order.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售订单表 列表VO
 *
 * @Author yxz
 * @Date 2025-10-23 14:10:32
 * @Copyright (c)2024 yxz
 */

@Data
public class SalesOrderVO {


    @Schema(description = "主键")
    private Long salesOrderId;

    @Schema(description = "单据编号-销售订单")
    private String billNo;

    @Schema(description = "单据日期")
    private LocalDate orderDate;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "销售员编码")
    private String salespersonCode;

    @Schema(description = "单据类型（0配件 1整车")
    private String orderType;

    @Schema(description = "价税合计")
    private BigDecimal amount;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "价税合计（本位币）")
    private BigDecimal fallAmount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
