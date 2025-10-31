package net.lab1024.sa.admin.module.vigorous.commission.calc.domain.dto;

import net.lab1024.sa.admin.module.vigorous.commission.calc.domain.entity.CommissionRecordEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;

import java.util.List;


public class CommissionExportDto extends CommissionRecordEntity {
    private String orderType; // 单据类型：整车/配件

    // 物料明细列表
    private List<ReceivablesDetailsEntity> materialItems;

}
