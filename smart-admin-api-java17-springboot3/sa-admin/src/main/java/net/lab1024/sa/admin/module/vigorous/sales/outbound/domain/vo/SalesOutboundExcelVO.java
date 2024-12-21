package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售出库 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:48:19
 * @Copyright (c)2024 yxz
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesOutboundExcelVO {
    @ExcelProperty("单据编号")
    private String billNo;

    @ExcelProperty(value = "日期")
    private LocalDate salesBoundDate;

    @ExcelProperty("客户")
    private String customerName;

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("金额")
    private BigDecimal amount;

}
