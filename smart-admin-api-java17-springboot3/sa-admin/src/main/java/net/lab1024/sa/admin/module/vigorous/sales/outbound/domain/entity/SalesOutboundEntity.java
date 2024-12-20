package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售出库 实体类
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_sales_outbound")
public class SalesOutboundEntity {

    /**
     * 主键
     */
    @TableId
    private Long salesBoundId;

    /**
     * 单据编号
     */
    private String billNo;

    /**
     * 出库日期
     */
    private LocalDate salesBoundDate;

    /**
     * 客户编码
     */
    private Long customerId;

    /**
     * 业务员编码
     */
    private Long salespersonId;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
