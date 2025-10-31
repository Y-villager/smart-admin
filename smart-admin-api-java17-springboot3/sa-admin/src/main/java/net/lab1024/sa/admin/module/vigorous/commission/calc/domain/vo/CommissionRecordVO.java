package

        net.lab1024.sa.admin.module.vigorous.commission.calc.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Schema(description = "销售订单-单据编号")
    private String salesOrderBillNo;

    @Schema(description = "订单-单据类型")
    private String orderType;

    @Schema(description = "销售日期")
    private LocalDate orderDate;

    @Schema(description = "发货通知单-单据编号")
    private String fSalesBillNo;

    @Schema(description = "销售出库-单据编号")
    private String salesBillNo;

    @Schema(description = "出库日期")
    private LocalDate outboundDate;

    @Schema(description = "应收单-单据编号")
    private String receiveBillNo;

    @Schema(description = "业务员id")
    private Long salespersonId;

    @Schema(description = "业务员名称")
    private String salespersonName;

    @Schema(description = "当时业务员级别id")
    private String currentSalespersonLevelName;

    @Schema(description = "当时业务员级别级别系数")
    private BigDecimal currentSalespersonLevelRate;

    @Schema(description = "客户id")
    private Long customerId;

    // 客户编码
    private String customerCode;

    // 客户名称
    private String customerName;

    // 首单日期
    private LocalDate firstOrderDate;

    // 调整后-首单日期
    private LocalDate adjustedFirstOrderDate;


    @Schema(description = "当时上级id")
    private Long currentParentId;

    // 当时上级名称
    private String currentParentName;

    @Schema(description = "当时上级级别id")
    private String currentParentLevelName;

    @Schema(description = "上级级别系数")
    private BigDecimal currentParentLevelRate;

    @Schema(description = "销售金额")
    private BigDecimal salesAmount;

    @Schema(description = "应收-币别")
    private String currencyType;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "税收合计（人民币")
    private BigDecimal fallAmount;

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

    private List<ReceivablesDetailsEntity> materialItems;

}
