package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 业务提成记录 实体类
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_commission_record")
public class CommissionRecordEntity {

    /**
     * 提成id
     */
    @TableId(type = IdType.AUTO)
    private Long commissionId;

    /**
     * 销售出库日期/ 业务日期
     */
    private LocalDate orderDate;

    /**
     * 销售出库-单据编号
     */
    private String salesBillNo;

    /**
     * 应收表-单据编号
     */
    private String receiveBillNo;

    /**
     * 业务员id
     */
    private Long salespersonId;

    /**
     * 业务员名称
     */
    private String salespersonName;

    /**
     * 客户id
     */
    private Long customerId;


    /**
     * 客户是否转交
     */
    private Integer isTransfer;

    /**
     * 客户是否报关
     */
    private Integer isCustomsDeclaration;

    /**
     * 当时业务员级别id
     */
    private String currentSalespersonLevelName;

    /**
     * 业务员级别系数
     */
    private BigDecimal currentSalespersonLevelRate;

    /**
     * 当时上级id
     */
    private Long currentParentId;


    private String currentParentName;

    /**
     * 上级级别id
     */
    private String currentParentLevelName;

    /**
     * 上级级别系数
     */
    private BigDecimal currentParentLevelRate;

    /**
     * 销售金额
     */
    private BigDecimal salesAmount;

    /**
     * 币别
     */
    private String currencyType;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 价税合计（人民币）
     */
    private BigDecimal fallAmount;

    /**
     * 用户合作年数
     */
    private Integer customerYear;

    /**
     * 客户年份系数
     */
    private BigDecimal customerYearRate;

    /**
     * 提成类别(1业务 2管理
     */
    private Integer commissionType;

    /**
     * 最终提成系数
     */
    private BigDecimal commissionRate;

    /**
     * 提成金额
     */
    private BigDecimal commissionAmount;


    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime updateTime;
}
