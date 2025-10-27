package

        net.lab1024.sa.admin.module.vigorous.fsales.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 发货通知单 列表VO
 *
 * @Author yxz
 * @Date 2025-10-23 14:11:35
 * @Copyright (c)2024 yxz
 */

@Data
public class FSalesVO {


    @Schema(description = "主键")
    private Long fSalesId;

    @Schema(description = "单据编号-发货通知单")
    private String billNo;

    @Schema(description = "单据日期")
    private LocalDate billDate;

    @Schema(description = "业务员编码")
    private String salespersonCode;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "源单编号-销售订单")
    private String originBillNo;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
