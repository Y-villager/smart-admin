package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 销售出库 分页查询表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SalesOutboundQueryForm extends PageParam {

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "业务员")
    private String salespersonCode;

    @Schema(description = "出库日期")
    private LocalDate salesBoundDate;

}
