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
    BUSINESS(1, "业务提成"),

    /**
     * 2 管理提成
     */
    MANAGEMENT(2, "管理提成"),

    ;

    private final Integer value;

    private final String desc;

    // 根据 value 获取对应的 desc
    public static String getDescByValue(Integer value) {
        for (CommissionTypeEnum type : CommissionTypeEnum.values()) {
            if (type.getValue().equals(value)) {
                return type.getDesc();
            }
        }
        return null; // 如果没有找到对应的 value，返回 null 或者可以抛出异常
    }
}
