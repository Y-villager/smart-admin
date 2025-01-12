package net.lab1024.sa.admin.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

@Getter
@AllArgsConstructor
public enum CommissionTypeEnum implements BaseEnum {
    /**
     * 1 业务提成
     */
    MANAGEMENT(1, "业务提成"),

    /**
     * 2 管理提成
     */
    BUSINESS(2, "管理提成"),

    ;

    private final Integer value;

    private final String desc;
}
