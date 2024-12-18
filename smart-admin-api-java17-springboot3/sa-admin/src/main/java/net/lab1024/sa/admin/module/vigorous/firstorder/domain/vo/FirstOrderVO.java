package net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Data;

/**
 * 客户首单信息 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@Data
public class FirstOrderVO {


    @Schema(description = "首单信息编号")
    private Long firstOrderId;

    @Schema(description = "客户编码")
    private String customerId;

    @Schema(description = "业务员编码")
    private String salespersonId;

    @Schema(description = "首单编号")
    private String billNo;

    @Schema(description = "首单日期")
    private LocalDate orderDate;

}
