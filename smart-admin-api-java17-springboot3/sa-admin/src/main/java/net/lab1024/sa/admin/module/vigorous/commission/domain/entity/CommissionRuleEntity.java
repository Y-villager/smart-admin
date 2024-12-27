package

        net.lab1024.sa.admin.module.vigorous.commission.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

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
    @TableId
    private Long ruleId;

    /**
     * 币种
     */
    private String currencyType;

    /**
     * 业务员级别id
     */
    private Integer salespersonLevelId;

    /**
     * 提成比例
     */
    private BigDecimal commissionRate;

    /**
     * 首单比例
     */
    private BigDecimal firstOrderRate;

    /**
     * 首年比例
     */
    private BigDecimal firstYearRate;

    /**
     * 逐年递减比例
     */
    private BigDecimal yearlyDecreaseRate;

    /**
     * 最低比例
     */
    private BigDecimal minRate;

    /**
     * 备注
     */
    private String remark;

}
