package net.lab1024.sa.admin.module.vigorous.customer.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.admin.enumeration.CustomerGroupEnum;
import net.lab1024.sa.admin.enumeration.TransferStatusEnum;
import net.lab1024.sa.base.common.swagger.SchemaEnum;

import javax.validation.constraints.NotBlank;

/**
 * 顾客 新建表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Data
public class CustomerAddForm {

    @Schema(description = "客户编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户编码 不能为空")
    private String customerCode;

    @Schema(description = "客户名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "客户名称 不能为空")
    private String customerName;

    @Schema(description = "简称")
    private String shortName;

    @Schema(description = "国家")
    private String country;

    @SchemaEnum(CustomerGroupEnum.class)
    private Integer customerGroup;

    @Schema(description = "客户类别")
    private String customerCategory;

    @Schema(description = "业务员", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员 不能为空")
    private String salespersonName;

    @SchemaEnum(TransferStatusEnum.class)
    private Integer transferStatus;


}