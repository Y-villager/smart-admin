package net.lab1024.sa.admin.module.vigorous.customer.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 顾客 实体类
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_customer")
public class CustomerEntity {

    /**
     * 主键
     */
    @TableId
    private Long customerId;

    /**
     * 客户名称
     */
    private String customerName;

    /**
     * 简称
     */
    private String shortName;

    /**
     * 国家
     */
    private String country;

    /**
     * 客户分组
     */
    private String customerGroup;

    /**
     * 客户类别
     */
    private String customerCategory;

    /**
     * 业务员编码
     */
    private Long salespersonId;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 首单信息编号
     */
    private Long firstOrderId;

}
