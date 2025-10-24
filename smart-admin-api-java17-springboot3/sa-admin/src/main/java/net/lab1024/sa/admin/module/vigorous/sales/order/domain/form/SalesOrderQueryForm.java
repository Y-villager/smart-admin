package net.lab1024.sa.admin.module.vigorous.sales.order.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 销售订单表 分页查询表单
 *
 * @Author yxz
 * @Date 2025-10-23 14:10:32
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class SalesOrderQueryForm extends PageParam {

    @Schema(description = "单据日期")
    private LocalDate orderDate;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "业务员")
    private String salespersonCode;

}
