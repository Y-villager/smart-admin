package net.lab1024.sa.admin.module.vigorous.firstorder.domain.form;

import net.lab1024.sa.base.common.domain.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 客户首单信息 分页查询表单
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class FirstOrderQueryForm extends PageParam {

    @Schema(description = "客户简称")
    private String CustomerId;

    @Schema(description = "业务员")
    private String SalespersonId;

}
