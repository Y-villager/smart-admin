package net.lab1024.sa.admin.module.vigorous.customer.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lab1024.sa.admin.module.vigorous.firstorder.domain.entity.FirstOrderEntity;
import net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity.SalespersonEntity;

/**
 * 顾客 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerVO {


    @Schema(description = "主键")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "简称")
    private String shortName;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "客户分组")
    private String customerGroup;

    @Schema(description = "客户类别")
    private String customerCategory;

    @Schema(description = "业务员")
    private SalespersonEntity salesperson;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "首单信息编号")
    private FirstOrderEntity firstOrder;

}
