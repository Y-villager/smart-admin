package net.lab1024.sa.admin.module.vigorous.fsales.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 发货通知单 新建表单
 *
 * @Author yxz
 * @Date 2025-10-23 14:11:35
 * @Copyright (c)2024 yxz
 */

@Data
public class FSalesAddForm {

    @Schema(description = "单据编号-发货通知单", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "单据编号-发货通知单 不能为空")
    private String billNo;

    @Schema(description = "单据日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单据日期 不能为空")
    private LocalDate billDate;

    @Schema(description = "业务员编码")
    private String salespersonCode;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "源单编号-销售订单")
    private String originBillNo;

}