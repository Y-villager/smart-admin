package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 业务提成记录 新建表单
 *
 * @Author yxz
 * @Date 2025-01-12 15:25:35
 * @Copyright (c)2024 yxz
 */

@Data
public class CommissionRecordImportForm {

    @ExcelProperty("销售员")
    private String salespersonName;

    @ExcelProperty("客户编码")
    private String customerCode;

    @ExcelProperty("提成类型")
    private Integer commissionType;

    @ExcelProperty("提成金额")
    private BigDecimal amout;

    @ExcelProperty("销售出库id")
    private Long salesBoundId;

}