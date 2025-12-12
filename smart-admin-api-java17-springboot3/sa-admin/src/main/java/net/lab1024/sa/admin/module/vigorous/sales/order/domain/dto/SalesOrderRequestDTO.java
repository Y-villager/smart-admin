package net.lab1024.sa.admin.module.vigorous.sales.order.domain.dto;

import lombok.Data;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.form.SalesOrderExcludeForm;
import net.lab1024.sa.admin.module.vigorous.sales.order.domain.form.SalesOrderQueryForm;

@Data
public class SalesOrderRequestDTO {
    private SalesOrderQueryForm queryForm;
    private SalesOrderExcludeForm excludeForm;
}
