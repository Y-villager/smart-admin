package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

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

    @Schema(description = "单据编号")
    private String billNo;

    @Schema(description = "客户名称")
    private String customerName;

    private Long customerId;

    @Schema(description = "业务员名称")
    private String salespersonName;

    private Long salespersonId;

    @Schema(description = "出库日期开始")
    private LocalDate salesBoundDateBegin;

    @Schema(description = "出库日期结束")
    private LocalDate salesBoundDateEnd;

}
