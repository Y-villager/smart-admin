package net.lab1024.sa.admin.module.vigorous.receivables.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应收明细表 分页查询表单
 *
 * @Author yxz
 * @Date 2025-10-23 14:43:51
 * @Copyright (c)2025 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class ReceivablesDetailsQueryForm extends PageParam {

    @Schema(description = "应收单")
    private String receivablesId;

    @Schema(description = "物料编码")
    private String materialCode;

    @Schema(description = "物料名称")
    private String materialName;

}
