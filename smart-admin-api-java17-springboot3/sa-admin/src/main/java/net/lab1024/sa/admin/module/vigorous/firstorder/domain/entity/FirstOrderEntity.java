package

        net.lab1024.sa.admin.module.vigorous.firstorder.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
    @TableId(type = IdType.AUTO)
    private Long firstOrderId;

    /**
     * 客户编码
     */
    private Long customerId;

    /**
     * 业务员编码
     */
    private Long salespersonId;

    /**
     * 首单编号
     */
    private String billNo;

    /**
     * 首单日期
     */
    private LocalDate orderDate;


    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
