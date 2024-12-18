package net.lab1024.sa.admin.module.vigorous.salesperson.domain.form;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 业务员 导入表单
 *
 */
@Data
public class SalespersonImportForm {

    @ExcelProperty("业务员编码")
    private String salespersonCode;

    @ExcelProperty("业务员名称")
    private String salespersonName;

    @ExcelProperty("部门")
    private String departmentName;

    @ExcelProperty("国家")
    private String country;

    @ExcelProperty("级别")
    private String salespersonLevel;

    private String errorMessage;

}
