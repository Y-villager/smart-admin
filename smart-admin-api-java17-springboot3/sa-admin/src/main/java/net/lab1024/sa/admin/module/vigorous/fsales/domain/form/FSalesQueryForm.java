package net.lab1024.sa.admin.module.vigorous.fsales.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 发货通知单 分页查询表单
 *
 * @Author yxz
 * @Date 2025-10-23 14:11:35
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class FSalesQueryForm extends PageParam {

    @Schema(description = "单据日期")
    private LocalDate billDate;

    @Schema(description = "单据日期")
    private String billNo;

    @Schema(description = "业务员编码")
    private String salespersonCode;

    @Schema(description = "客户编码")
    private String customerCode;

}
