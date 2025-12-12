package

        net.lab1024.sa.admin.module.vigorous.sales.order.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 销售订单表 实体类
 *
 * @Author yxz
 * @Date 2025-10-23 14:10:32
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_sales_order")
@AllArgsConstructor
@NoArgsConstructor
public class SalesOrderEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long salesOrderId;

    /**
     * 单据编号-销售订单
     */
    private String billNo;

    /**
     * 单据日期
     */
    private LocalDate orderDate;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 销售员编码
     */
    private String salespersonCode;

    /**
     * 单据类型（0配件 1整车
     */
    private String orderType;

    /**
     * 价税合计
     */
    private BigDecimal amount;

    /**
     * 汇率
     */
    private BigDecimal exchangeRate;

    /**
     * 价税合计（本位币）
     */
    private BigDecimal fallAmount;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
