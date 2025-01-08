package

        net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客户首单信息 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FirstOrderVO {

    @Schema(description = "首单信息编号")
    private Long firstOrderId;

    @Schema(description = "客户编码")
    private Long customerId;

    private String customerName;
    private String customerCode;

    @Schema(description = "业务员编码")
    private Long salespersonId;

    private String salespersonName;

    @Schema(description = "首单编号")
    private String billNo;

    @Schema(description = "首单日期")
    private LocalDate orderDate;

    @Schema(description = "创建日期")
    private LocalDateTime createTime;

    @Schema(description = "修改日期")
    private LocalDateTime updateTime;

}
