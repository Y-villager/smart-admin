package net.lab1024.sa.admin.module.vigorous.salesperson.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 业务员 实体类
 *
 * @Author yxz
 * @Date 2024-12-12 14:42:49
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
     * 部门
     */
    private String department;

    /**
     * 职位
     */
    private String position;

    /**
     * 禁用状态
     */
    private Integer status;

    /**
     * 层级路径
     */
    private String path;

    /**
     * 假删除
     */
    private Integer deletedFlag;

}
