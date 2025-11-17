package net.lab1024.sa.admin.enumeration;

import java.util.stream.Stream;

public enum OrderTypeEnum {
    /**
     * 订单类型枚举定义
     */
    VEHICLE_SALE("整车销售订单"),       // 整车销售
    ACCESSORY_SALE("配件销售订单"),    // 配件销售
    INSULATED_BOX_SALE("保温箱销售订单"); // 保温箱销售

    private final String displayName;

    // 构造方法
    OrderTypeEnum(String displayName) {
        this.displayName = displayName;
    }

    // 获取显示名称
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据显示名称反向查找枚举
     * @param displayName 显示名称
     * @return 匹配的枚举或null
     */
    public static OrderTypeEnum fromDisplayName(String displayName) {
        return Stream.of(values())
                .filter(type -> type.getDisplayName().equals(displayName))
                .findFirst()
                .orElse(null);
    }

    // 示例使用
    public static void main(String[] args) {
        // 遍历所有订单类型
        for (OrderTypeEnum type : OrderTypeEnum.values()) {
            System.out.printf("枚举常量: %s, 显示名称: %s%n",
                    type.name(),
                    type.getDisplayName());
        }

        // 通过名称获取枚举
        OrderTypeEnum vehicle = OrderTypeEnum.valueOf("VEHICLE_SALE");
        System.out.println("通过常量名获取: " + vehicle.getDisplayName());

        // 通过显示名称获取枚举
        OrderTypeEnum accessory = OrderTypeEnum.fromDisplayName("配件销售订单");
        System.out.println("通过显示名称获取: " + accessory);
    }
}