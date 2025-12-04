package net.lab1024.sa.admin.module.vigorous.customer.domain.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.lab1024.sa.base.common.domain.PageParam;

import java.time.LocalDate;

/**
 * 顾客 分页查询表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerQueryForm extends PageParam {

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "客户")
    private String customerName;

    @Schema(description = "业务员")
    private String salespersonName;

    @Schema(description = "客户分组")
    private String customerGroup;

    @Schema(description = "是否存在首单")
    private Boolean hasFirstOrder;

    @Schema(description = "金蝶-创建日期-开始")
    private LocalDate createDateBegin;

    @Schema(description = "金蝶-创建日期-结束")
    private LocalDate createDateEnd;

    @Schema(description = "国家")
    private String country;

}
