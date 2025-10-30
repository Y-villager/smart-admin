package net.lab1024.sa.admin.module.vigorous.receivables.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 应收明细表 新建表单
 *
 * @Author yxz
 * @Date 2025-10-23 14:43:51
 * @Copyright (c)2025 yxz
 */

@Data
public class ReceivablesDetailsAddForm {

    @Schema(description = "物料编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "物料编码 不能为空")
    private String materialCode;

    @Schema(description = "物料名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "物料名称 不能为空")
    private String materialName;

    @Schema(description = "应收单id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "应收单id 不能为空")
    private String originBillNo;

}