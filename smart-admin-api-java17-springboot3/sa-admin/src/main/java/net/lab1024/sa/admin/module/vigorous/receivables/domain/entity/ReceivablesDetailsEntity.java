package net.lab1024.sa.admin.module.vigorous.receivables.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 应收明细表 实体类
 *
 * @Author yxz
 * @Date 2025-10-23 14:43:51
 * @Copyright (c)2025 yxz
 */

@Data
@TableName("v_receivables_details")
public class ReceivablesDetailsEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 物料编码
     */
    private String materialCode;

    /**
     * 物料名称
     */
    private String materialName;

    /**
     * 销售单位
     */
    private String saleUnit;

    /**
     * 销售数量
     */
    private Integer saleQuantity;

    /**
     * 应收单id
     */
    private String originBillNo;

    /**
     * 序号
     */
    private Integer serialNum;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
