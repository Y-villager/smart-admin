package

        net.lab1024.sa.admin.module.vigorous.commission.rule.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 提成规则 实体类
 *
 * @Author yxz
 * @Date 2024-12-23 16:14:00
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_commission_rule")
public class CommissionRuleEntity {

    /**
     * 提成规则id
     */
    @TableId(type = IdType.AUTO)
    private Long ruleId;

    /**
     * 提成类型（1业务 2管理）
     */
    private Integer commissionType;

    /**
     * 转交状态（0自主开发，非0转交）
     */
    private Integer transferStatus;

    /**
     * 是否报关(0不需要报关 1需要报关)
     */
    private Integer isCustomsDeclaration;


    /**
     * 是否计算公式（0否 1是）
     */
    private Integer useDynamicFormula;

    /**
     * 提成系数
     */
    private BigDecimal commissionRate;

    /**
     * 计算公式id
     */
    private Long formulaId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
