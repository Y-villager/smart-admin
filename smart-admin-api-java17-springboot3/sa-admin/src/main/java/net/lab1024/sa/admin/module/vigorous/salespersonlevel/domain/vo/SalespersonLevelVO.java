package net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.Data;

/**
 * 业务员级别 列表VO
 *
 * @Author yxz
 * @Date 2024-12-14 16:07:14
 * @Copyright (c)2024 yxz
 */

@Data
public class SalespersonLevelVO {


    @Schema(description = "业务员级别编码")
    private Integer salespersonLevelId;

    @Schema(description = "业务员级别名称")
    private String salespersonLevelName;

    @Schema(description = "提成比例")
    private BigDecimal commissionRate;

}
