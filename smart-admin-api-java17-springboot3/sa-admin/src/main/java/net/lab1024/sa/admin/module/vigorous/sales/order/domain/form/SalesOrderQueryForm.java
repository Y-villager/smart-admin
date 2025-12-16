package net.lab1024.sa.admin.module.vigorous.sales.order.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

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

    @Schema(description = "销售订单")
    private String billNo;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "业务员名称")
    private String salespersonName;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "单据日期开始")
    private LocalDate orderDateBegin;

    @Schema(description = "单据日期结束")
    private LocalDate orderDateEnd;

}
