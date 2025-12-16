package net.lab1024.sa.admin.module.vigorous.receivables.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 应收明细表 列表VO
 *
 * @Author yxz
 * @Date 2025-10-23 14:43:51
 * @Copyright (c)2025 yxz
 */

@Data
@Builder
public class ReceivablesDetailsVO {

    @Schema(description = "主键")
    private Integer id;

    @Schema(description = "应收单-单据编号")
    private String originBillNo;

    @Schema(description = "物料编码")
    private String materialCode;

    @Schema(description = "物料名称")
    private String materialName;

    @Schema(description = "批号")
    private String serialBatch;

    @Schema(description = "销售单位")
    private String saleUnit;

    @Schema(description = "销售数量")
    private Integer saleQuantity;

    @Schema(description = "销售数量")
    private BigDecimal saleAmount;

    @Schema(description = "创建日期")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
