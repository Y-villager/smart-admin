package net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 业务员 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:42:49
 * @Copyright (c)2024 yxz
 */

@Data
public class SalespersonVO {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "业务员编码")
    private String salespersonCode;

    @Schema(description = "业务员名称")
    private String salespersonName;

    @Schema(description = "部门")
    private String department;

    @Schema(description = "职位")
    private String position;

    @Schema(description = "禁用状态")
    private Integer status;

}
