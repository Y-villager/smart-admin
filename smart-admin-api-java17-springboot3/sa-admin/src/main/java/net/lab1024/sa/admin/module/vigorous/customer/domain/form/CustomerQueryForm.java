package net.lab1024.sa.admin.module.vigorous.customer.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @Schema(description = "顾客名称")
    private String CustomerName;

    @Schema(description = "简称")
    private String ShortName;

    @Schema(description = "业务员")
    private String SalespersonId;

}
