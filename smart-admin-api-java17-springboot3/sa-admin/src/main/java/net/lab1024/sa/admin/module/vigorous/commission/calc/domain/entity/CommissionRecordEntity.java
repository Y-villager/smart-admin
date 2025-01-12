package

        net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

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
     * 业务员id
     */
    private Long salespersonId;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 提成类型(0业务1管理）
     */
    private Integer commissionType;

    /**
     * 提成金额
     */
    private BigDecimal amout;

    /**
     * 销售出库id
     */
    private Long salesOutboundId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;

}
