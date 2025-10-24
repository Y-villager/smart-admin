package net.lab1024.sa.admin.module.vigorous.sales.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售订单表 新建表单
 *
 * @Author yxz
 * @Date 2025-10-23 14:10:32
 * @Copyright (c)2024 yxz
 */

@Data
public class SalesOrderAddForm {

    @Schema(description = "单据编号-销售订单", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "单据编号-销售订单 不能为空")
    private String billNo;

    @Schema(description = "单据日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单据日期 不能为空")
    private LocalDate orderDate;

    @Schema(description = "客户编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户编码 不能为空")
    private String customerCode;

    @Schema(description = "销售员编码")
    private String salespersonCode;

    @Schema(description = "单据类型（0配件 1整车", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单据类型（0配件 1整车 不能为空")
    private String orderType;

    @Schema(description = "价税合计")
    private BigDecimal amount;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "价税合计（本位币）")
    private BigDecimal fallAmount;

}