package net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 业务员 列表VO
 *
 * @Author yxz
 * @Date 2024-12-16 10:56:45
 * @Copyright (c)2024 yxz
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalespersonVO {
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "业务员编码")
    private String salespersonCode;

    @Schema(description = "业务员名称")
    private String salespersonName;

    @Schema(description = "部门编码")
    private Long departmentId;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "级别编码")
    private Integer salespersonLevelId;

    @Schema(description = "级别名称")
    private String salespersonLevelName;

    @Schema(description = "层级路径")
    private String path;

    @Schema(description = "上级id")
    private Long parentId;

}
