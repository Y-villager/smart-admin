package

        net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 业务提成记录 列表VO
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommissionRecordVO {


    @Schema(description = "提成id")
    private Long commissionId;

    @Schema(description = "销售出库id")
    private Long salesOutboundId;

    @Schema(description = "业务员id")
    private Long salespersonId;

    private String salespersonName;

    @Schema(description = "销售出库-单据编号")
    private String salesBillNo;

    @Schema(description = "应收单-单据编号")
    private String receivableBillNo;

    @Schema(description = "客户id")
    private Long customerId;

    private String customerName;

    private LocalDate adjustedFirstOrderDate;

    private LocalDate firstOrderDate;

    @Schema(description = "销售金额")
    private BigDecimal salesAmount;

    @Schema(description = "销售出库日期/业务日期")
    private LocalDate orderDate;

    private String currencyType;
    // 是否为转交客户
    private Integer transferStatus;

    // 提成标识（）
    private Integer commissionFlag;

    @Schema(description = "客户合作年数")
    private Integer customerYear;

    @Schema(description = "客户系数")
    private BigDecimal customerYearRate;

    @Schema(description = "业务提成系数")
    private BigDecimal businessCommissionRate;

    @Schema(description = "业务提成金额")
    private BigDecimal businessCommissionAmount;

    @Schema(description = "管理提成系数")
    private BigDecimal managementCommissionRate;
    @Schema(description = "管理提成金额")
    private BigDecimal managementCommissionAmount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建时间")
    private LocalDateTime updateTime;

    @Schema(description = "备注")
    private String remark;

}
