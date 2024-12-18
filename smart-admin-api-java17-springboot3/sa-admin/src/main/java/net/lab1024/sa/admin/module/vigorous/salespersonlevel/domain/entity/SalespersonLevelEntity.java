package net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 业务员级别 实体类
 *
 * @Author yxz
 * @Date 2024-12-14 16:07:14
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_salesperson_level")
public class SalespersonLevelEntity {

    /**
     * 业务员级别编码
     */
    @TableId
    private Integer salespersonLevelId;

    /**
     * 业务员级别名称
     */
    private String salespersonLevelName;

    /**
     * 提成比例
     */
    private BigDecimal commissionRate;

}
