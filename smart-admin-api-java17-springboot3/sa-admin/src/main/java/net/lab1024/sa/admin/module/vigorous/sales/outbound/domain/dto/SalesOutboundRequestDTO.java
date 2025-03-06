package net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.dto;

import lombok.Data;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundExcludeForm;
import net.lab1024.sa.admin.module.vigorous.sales.outbound.domain.form.SalesOutboundQueryForm;

@Data
public class SalesOutboundRequestDTO {
    private SalesOutboundQueryForm queryForm;
    private SalesOutboundExcludeForm excludeForm;
}
