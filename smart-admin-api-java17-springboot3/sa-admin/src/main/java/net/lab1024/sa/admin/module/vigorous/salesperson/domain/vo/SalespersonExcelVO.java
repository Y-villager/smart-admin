package net.lab1024.sa.admin.module.vigorous.salesperson.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SalespersonExcelVO {
    @ExcelProperty("业务员编码")
    private String salespersonCode;

    @ExcelProperty("业务员名称")
    private String salespersonName;

    @ExcelProperty("业务员级别")
    private String salespersonLevel;

    @ExcelProperty("部门")
    private String department;

    @ExcelProperty("上级业务员")
    private String parentName;


}
