package net.lab1024.sa.admin.module.vigorous.receivables.domain.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReceivablesMaterialEntity {
    private Long id;

    private String originBillNo;

    private String materialCode;

    private String materialName;

    private Integer saleQuantity;

    private String saleUnit;

    private BigDecimal materialAmount;

    private Integer sortOrder;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

}