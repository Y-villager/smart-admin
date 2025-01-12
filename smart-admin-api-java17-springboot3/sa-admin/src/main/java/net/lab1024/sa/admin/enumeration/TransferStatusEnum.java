package net.lab1024.sa.admin.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

@AllArgsConstructor
@Getter
public enum TransferStatusEnum implements BaseEnum {

    INDEPENDENTLY(0, "自主开发"),

    RESIGNATION_TRANSFER(1, "离职转交"),

    INTERNAL_HANDOVER(2, "内勤转交"),

    COMPANY_TRANSFER(3, "公司转交"),

    COMPANY_CUSTOMER(4, "公司客户"),

    OTHER(5, "其他转交")
    ;

    private final Integer value;

    private final String desc;
}
