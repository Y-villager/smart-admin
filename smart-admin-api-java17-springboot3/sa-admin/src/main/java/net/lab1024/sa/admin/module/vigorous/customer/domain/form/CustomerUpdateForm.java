package net.lab1024.sa.admin.module.vigorous.customer.domain.form;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import net.lab1024.sa.base.common.json.deserializer.DictValueVoDeserializer;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 顾客 更新表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Data
public class CustomerUpdateForm {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "主键 不能为空")
    private Long customerId;

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

    @Schema(description = "客户分组")
    private String customerGroup;

    @Schema(description = "客户类别")
    private String customerCategory;

    @Schema(description = "币别")
    @JsonDeserialize(using = DictValueVoDeserializer.class)
    private String currencyType;

    @Schema(description = "首单日期")
    private LocalDate firstOrderDate;

    @Schema(description = "业务员编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "业务员编码 不能为空")
    private Long salespersonId;

    @Schema(description = "转交状态")
    private Integer transferStatus;

    @Schema(description = "是否报关")
    private Integer isCustomsDeclaration;

    @Schema(description = "金蝶-创建日期")
    private LocalDate createDate;
}