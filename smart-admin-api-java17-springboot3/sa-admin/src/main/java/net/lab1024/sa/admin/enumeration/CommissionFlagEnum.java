package net.lab1024.sa.admin.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.lab1024.sa.base.common.enumeration.BaseEnum;

@Getter
@AllArgsConstructor
public enum CommissionFlagEnum implements BaseEnum{

    NONE(0, "未生成"),

    CREATED(1, "已生成"),

    OVERRIDABLE(2, "修改中"),

    ;

    private final Integer value;

    private final String desc;
}
