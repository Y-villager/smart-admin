package net.lab1024.sa.admin.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

@Getter
@AllArgsConstructor
public enum SystemYesNo implements BaseEnum {
    /**
     * 否
     */
    NO(0, "否"),

    /**
     * 是
     */
    YES(1, "是"),


    ;

    private final Integer value;

    private final String desc;
}
