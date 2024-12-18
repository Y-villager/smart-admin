package net.lab1024.sa.admin.module.vigorous.customer.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 顾客 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */
@Data
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

    @Schema(description = "业务员编码")
    private Long salespersonId;

    @Schema(description = "业务员")
    private String salespersonName;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "首单信息编号")
    private Long firstOrderId;

}
