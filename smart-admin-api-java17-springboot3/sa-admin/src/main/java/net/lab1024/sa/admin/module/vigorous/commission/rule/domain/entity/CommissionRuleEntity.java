package

        net.lab1024.sa.admin.module.vigorous.commission.rule.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    @TableId(type = IdType.AUTO)
    private Long ruleId;

    /**
     * 业务员级别id
     */
    private Integer salespersonLevelId;

    /**
     * 客户分组(1内贸 2外贸)
     */
    private Integer customerGroup;

    /**
     * 首单比例
     */
    private BigDecimal firstOrderRate;

    /**
     * 基础比例
     */
    private BigDecimal baseRate;

    /**
     * 备注
     */
    private String remark;

}
