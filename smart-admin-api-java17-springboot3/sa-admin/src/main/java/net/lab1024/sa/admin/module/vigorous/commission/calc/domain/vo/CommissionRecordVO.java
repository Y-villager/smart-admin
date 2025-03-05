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

    // 销售出库id
    private Long salesOutboundId;

    @Schema(description = "销售出库-单据编号")
    private String salesBillNo;

    @Schema(description = "应收单-单据编号")
    private String receiveBillNo;

    @Schema(description = "销售出库日期/业务日期")
    private LocalDate orderDate;

    @Schema(description = "业务员id")
    private Long salespersonId;

    // 业务员名称
    private String salespersonName;

    @Schema(description = "当时业务员级别id")
    private Integer currentSalespersonLevelId;

    @Schema(description = "当时业务员级别级别系数")
    private BigDecimal currentSalespersonLevelRate;

    @Schema(description = "客户id")
    private Long customerId;

    // 客户编码
    private String customerCode;

    // 客户名称
    private String customerName;


    @Schema(description = "当时上级id")
    private Long currentParentId;

    // 当时上级名称
    private String currentParentName;

    @Schema(description = "当时上级级别id")
    private Integer currentParentLevelId;

    @Schema(description = "上级级别系数")
    private BigDecimal currentParentLevelRate;

    // 首单日期
    private LocalDate firstOrderDate;

    // 调整后-首单日期
    private LocalDate adjustedFirstOrderDate;

    @Schema(description = "销售金额")
    private BigDecimal salesAmount;

    // 应收-币别
    private String currencyType;

    // 提成标识（0未生成 1已生成 2编辑中）
    private Integer commissionFlag;

    @Schema(description = "客户合作年数")
    private Integer customerYear;

    @Schema(description = "客户系数")
    private BigDecimal customerYearRate;

    @Schema(description = "提成类别")
    private Integer commissionType;

    @Schema(description = "提成系数")
    private BigDecimal commissionRate;

    @Schema(description = "提成金额")
    private BigDecimal commissionAmount;

    @Schema(description = "客户是否转交")
    private Integer isTransfer;

    @Schema(description = "客户是否报关")
    private Integer isCustomsDeclaration;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建时间")
    private LocalDateTime updateTime;

    @Schema(description = "备注")
    private String remark;

}
