package net.lab1024.sa.admin.module.vigorous.salespersonlevel.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 业务员级别 列表VO
 *
 * @Author yxz
 * @Date 2024-12-14 16:07:14
 * @Copyright (c)2024 yxz
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalespersonLevelExcelVO {

    @ExcelProperty("业务员级别编码")
    private Integer salespersonLevelId;

    @ExcelProperty("业务员级别名称")
    private String salespersonLevelName;

    @ExcelProperty("提成比例")
    private BigDecimal commissionRate;

}
