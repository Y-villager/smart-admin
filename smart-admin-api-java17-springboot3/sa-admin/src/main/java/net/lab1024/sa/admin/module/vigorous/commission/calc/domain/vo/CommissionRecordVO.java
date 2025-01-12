package

        net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 业务提成记录 列表VO
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Data
public class CommissionRecordVO {


    @Schema(description = "提成id")
    private Long commissionId;

    @Schema(description = "业务员id")
    private Long salespersonId;

    @Schema(description = "客户id")
    private Long customerId;

    @Schema(description = "提成类型(0业务1管理）")
    private Integer commissionType;

    @Schema(description = "提成金额")
    private BigDecimal amout;

    @Schema(description = "销售出库id")
    private Long salesOutboundId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    private String remark;

}
