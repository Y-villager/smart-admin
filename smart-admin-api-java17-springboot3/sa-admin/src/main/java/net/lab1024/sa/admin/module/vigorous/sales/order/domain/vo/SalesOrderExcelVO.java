package

        net.lab1024.sa.admin.module.vigorous.sales.order.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 销售订单表 列表VO
 *
 * @Author yxz
 * @Date 2025-10-23 14:10:32
 * @Copyright (c)2024 yxz
 */

@Data
@Builder
public class SalesOrderExcelVO {

    @ExcelProperty("单据编号")
    private String billNo;

    @ExcelProperty("单据日期")
    private LocalDate orderDate;

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("销售员编码")
    private String salespersonCode;

    @ExcelProperty("单据类型")
    private String orderType;

    @ExcelProperty("价税合计")
    private BigDecimal amount;

    @ExcelProperty("汇率")
    private BigDecimal exchangeRate;

    @ExcelProperty("价税合计（本位币）")
    private BigDecimal fallAmount;

    @ExcelProperty("错误信息")
    private String errorMsg;

}
