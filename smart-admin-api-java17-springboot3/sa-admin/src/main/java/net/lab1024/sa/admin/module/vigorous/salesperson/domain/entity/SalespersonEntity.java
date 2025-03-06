package net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 业务员 实体类
 *
 * @Author yxz
 * @Date 2024-12-16 10:56:45
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_salesperson")
public class SalespersonEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务员编码
     */
    private String salespersonCode;

    /**
     * 业务员名称
     */
    private String salespersonName;

    /**
     * 部门编码
     */
    private Long departmentId;

    /**
     * 级别编码
     */
    private Integer salespersonLevelId;

    /**
     * 层级路径
     */
    private String path;

    /**
     * 删除标识
     */
    private Boolean deletedFlag;

    /**
     * 禁用状态
     */
    private Boolean disabledFlag;

    /**
     * 上级id
     */
    private Long parentId;

}
