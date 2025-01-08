package

        net.lab1024.sa.admin.module.vigorous.firstorder.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 客户首单信息 列表VO
 *
 * @Author yxz
 * @Date 2024-12-12 14:50:26
 * @Copyright (c)2024 yxz
 */

@Builder
@Data
public class FirstOrderExcelVO {
    @ExcelProperty("销售出库-单据编号")
    private String billNo;

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("客户名称")
    private String customerName;

    @ExcelProperty( "业务员编码")
    private String salespersonCode;

    @ExcelProperty("销售员名称")
    private String salespersonName;

    @ExcelProperty( "首单日期")
    private LocalDate orderDate;

}
