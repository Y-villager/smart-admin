package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.admin.module.vigorous.common.vo.DateVO;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;
import java.util.List;

/**
 * 业务提成记录 分页查询表单
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class CommissionRecordQueryForm extends PageParam {

    @Schema(description = "业务员")
    private Integer salespersonId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "销售出库-单据编号")
    private String salesBillNo;

    @Schema(description = "销售订单-单据编号")
    private String salesOrderBillNo;

    @Schema(description = "销售开始日期")
    private LocalDate orderDateBegin;

    @Schema(description = "销售结束日期")
    private LocalDate orderDateEnd;

    @Schema(description = "出库开始日期")
    private LocalDate outboundDateBegin;

    @Schema(description = "出库结束日期")
    private LocalDate outboundDateEnd;

    @Schema(description = "提成类别")
    private Integer commissionType;

    @Schema(description = "是否报关")
    private Integer isCustomsDeclaration;

    @Schema(description = "是否包含物料明细")
    private Boolean isContainsMaterial;

    @Schema(description = "筛选日期列表")
    private List<DateVO> filterDateList;

    @Schema(description = "筛选日期列表")
    private Boolean isTotal;

}
