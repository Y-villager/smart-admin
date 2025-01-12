package

        net.lab1024.sa.admin.module.vigorous.customer.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 顾客 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:51:07
 * @Copyright (c)2024 yxz
 */

@Data
public class CustomerVO {


    @Schema(description = "主键")
    private Long customerId;

    @Schema(description = "客户名称")
    private String customerName;

    @Schema(description = "简称")
    private String shortName;

    @Schema(description = "国家")
    private String country;

    @Schema(description = "客户类别")
    private Integer customerCategory;

    @Schema(description = "业务员编码")
    private Long salespersonId;

    private String salespersonName;

    @Schema(description = "客户编码")
    private String customerCode;

    @Schema(description = "首单日期")
    private LocalDate firstOrderDate;

    @Schema(description = "转交状态")
    private Integer transferStatus;

    @Schema(description = "转交历史")
    private String transferHistory;

    @Schema(description = "客户分组")
    private Integer customerGroup;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
