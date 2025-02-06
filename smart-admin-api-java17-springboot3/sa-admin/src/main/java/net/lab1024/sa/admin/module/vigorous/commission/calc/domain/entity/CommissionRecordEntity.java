package

        net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity;

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
     * 销售出库id
     */
    private Long salesOutboundId;

    /**
     * 业务员id
     */
    private Long salespersonId;

    /**
     * 客户id
     */
    private Long customerId;

    /**
     * 销售出库-单据编号
     */
    private String salesBillNo;

    /**
     * 销售金额
     */
    private BigDecimal salesAmount;

    /**
     * 用户合作年数
     */
    private Integer customerYear;
    private BigDecimal customerYearRate;

    private BigDecimal businessCommissionAmount;
    private BigDecimal businessCommissionRate;

    private BigDecimal managementCommissionAmount;
    private BigDecimal managementCommissionRate;


    /**
     * 销售出库日期/ 业务日期
     */
    private LocalDate orderDate;


    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     *
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    private String remark;

}
