package net.lab1024.sa.admin.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

@Getter
@AllArgsConstructor
public enum CustomesDeclarationEnum implements BaseEnum {
    /**
     * 0 不报关
     */
    NOT_REQUIRED(0, "不报关"),

    /**
     * 1 报关
     */
    REQUIRED(1, "报关"),

    ;

    private final Integer value;

    private final String desc;
}
