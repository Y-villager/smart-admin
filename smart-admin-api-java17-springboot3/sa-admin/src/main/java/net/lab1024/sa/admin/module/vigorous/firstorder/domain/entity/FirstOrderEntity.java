package net.lab1024.sa.admin.module.vigorous.firstorder.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * 客户首单信息 实体类
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_first_order")
public class FirstOrderEntity {

    /**
     * 首单信息编号
     */
    @TableId
    private Long firstOrderId;

    /**
     * 客户编码
     */
    private String customerId;

    /**
     * 业务员编码
     */
    private String salespersonId;

    /**
     * 首单编号
     */
    private String billNo;

    /**
     * 首单日期
     */
    private LocalDate orderDate;

}
