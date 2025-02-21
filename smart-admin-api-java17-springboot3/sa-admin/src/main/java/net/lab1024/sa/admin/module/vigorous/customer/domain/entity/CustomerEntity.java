package

        net.lab1024.sa.admin.module.vigorous.customer.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @TableId(type = IdType.AUTO)
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
     * 客户类别
     */
    private Integer customerCategory;

    /**
     * 客户类别
     */
    private String currencyType;

    /**
     * 业务员编码
     */
    private Long salespersonId;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 首单日期
     */
    private LocalDate firstOrderDate;

    private LocalDate adjustedFirstOrderDate;

    /**
     * 是否转交
     */
    private Integer transferStatus;

    /**
     * 转交历史
     */
    private String transferHistory;

    /**
     * 客户分组 （1内贸 2外贸
     */
    private Integer customerGroup;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
