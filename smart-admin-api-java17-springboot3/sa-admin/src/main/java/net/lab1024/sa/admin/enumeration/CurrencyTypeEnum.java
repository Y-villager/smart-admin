package net.lab1024.sa.admin.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

@Getter
@AllArgsConstructor
public enum CurrencyTypeEnum implements BaseEnum {
    /**
     * 1 人民币
     */
    CNY("CNY", "人民币"),

    /**
     * 2 美元
     */
    USD("USD", "美元"),

    ;

    private final String value;

    private final String desc;
}
