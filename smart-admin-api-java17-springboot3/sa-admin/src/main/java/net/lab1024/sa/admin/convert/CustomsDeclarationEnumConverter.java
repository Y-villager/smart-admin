package net.lab1024.sa.admin.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import net.lab1024.sa.admin.enumeration.CustomesDeclarationEnum;

public class CustomsDeclarationEnumConverter implements Converter<Integer> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;  // 支持的 Java 类型
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;  // 支持的 Excel 数据类型
    }

    /**
     * 将 Java 中的 Integer 转换为 Excel 中的描述字符串
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) {
        Integer value = context.getValue();

        if (value == null) {
            return new WriteCellData<>("");  // 如果值为空，返回空字符串
        }

        // 遍历枚举项，找到对应的描述
        for (CustomesDeclarationEnum enumValue : CustomesDeclarationEnum.values()) {
            if (enumValue.getValue().equals(value)) {
                return new WriteCellData<>(enumValue.getDesc());  // 返回对应的描述，包装成 WriteCellData
            }
        }

        return new WriteCellData<>("");  // 如果没有匹配到对应的枚举值，返回空字符串
    }

    /**
     * 将 Excel 单元格中的字符串转换为对应的 Java 枚举值
     */
    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        String value = context.getReadCellData().getStringValue();

        if (value == null || value.isEmpty()) {
            return null;  // 空值处理
        }

        // 遍历枚举项，找到对应的值
        for (CustomesDeclarationEnum enumValue : CustomesDeclarationEnum.values()) {
            if (enumValue.getDesc().equals(value)) {
                return enumValue.getValue();  // 返回对应的枚举值
            }
        }

        return null; // 如果没有找到匹配的，返回 null
    }
}
