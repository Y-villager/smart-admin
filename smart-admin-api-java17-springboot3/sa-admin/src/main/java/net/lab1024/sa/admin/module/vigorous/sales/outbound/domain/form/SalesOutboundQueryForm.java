package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
public class SalesOutboundQueryForm extends PageParam {

    @Schema(description = "单据编号")
    private String billNo;

    @Schema(description = "源单编号")
    private String originBillNo;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "业务员名称")
    private String salespersonName;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "出库日期开始")
    private LocalDate salesBoundDateBegin;

    @Schema(description = "出库日期结束")
    private LocalDate salesBoundDateEnd;

    @Schema(description = "是否存在首单时间")
    private Boolean hasFirstOrder;

    @Schema(description = "提成标识")
    private Integer commissionFlag;

    private String errorMsg;
}
