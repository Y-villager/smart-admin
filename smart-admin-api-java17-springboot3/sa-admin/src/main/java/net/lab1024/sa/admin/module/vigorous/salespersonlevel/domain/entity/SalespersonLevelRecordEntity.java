package

        net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 业务员级别变动记录 实体类
 *
 * @Author yxz
 * @Date 2025-01-07 08:58:41
 * @Copyright (c)2024 yxz
 */

@Data
@TableName("v_salesperson_level_record")
public class SalespersonLevelRecordEntity {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 业务员id
     */
    private Long salespersonId;

    /**
     * 先前级别
     */
    private Integer oldLevel;

    /**
     * 现在级别
     */
    private Integer newLevel;

    /**
     * 变动日期
     */
    private LocalDate changeDate;

    /**
     * 变动原因
     */
    private String changeReason;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
