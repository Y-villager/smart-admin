package

        net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 业务员级别变动记录 列表VO
 *
 * @Author yxz
 * @Date 2025-01-07 08:58:41
 * @Copyright (c)2024 yxz
 */

@Data
public class SalespersonLevelRecordVO {


    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "业务员id")
    private Long salespersonId;

    private String salespersonName;

    @Schema(description = "先前级别")
    private Integer oldLevel;

    @Schema(description = "现在级别")
    private Integer newLevel;

    @Schema(description = "变动日期")
    private LocalDate changeDate;

    @Schema(description = "变动原因")
    private String changeReason;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
