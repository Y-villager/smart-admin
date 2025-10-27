package

        net.lab1024.sa.admin.module.vigorous.fsales.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 发货通知单 实体类
 *
 * @Author yxz
 * @Date 2025-10-23 14:11:35
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_f_sales")
public class FSalesEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long fSalesId;

    /**
     * 单据编号-发货通知单
     */
    private String billNo;

    /**
     * 单据日期
     */
    private LocalDate billDate;

    /**
     * 业务员编码
     */
    private String salespersonCode;

    /**
     * 客户编码
     */
    private String customerCode;

    /**
     * 源单编号-销售订单
     */
    private String originBillNo;

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
