package net.lab1024.sa.admin.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;

public class FlexibleDateConverter implements Converter<LocalDate> {

    // 支持的输入格式（兼容 "/" 分隔符和单/双位月日）
    private static final List<DateTimeFormatter> INPUT_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd")
    );

    // 输出的目标格式（不补零，如 "2000-2-2"）
    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-M-d");

    @Override
    public Class<LocalDate> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    // 解析 Excel 数据 → LocalDate
    @Override
    public LocalDate convertToJavaData(
            ReadCellData<?> cellData,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration
    ) {
        String dateStr = cellData.getStringValue();
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        // 尝试所有格式解析日期
        for (DateTimeFormatter formatter : INPUT_FORMATTERS) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
                // 继续尝试下一个格式
            }
        }

        throw new IllegalArgumentException("无法解析日期: " + dateStr);
    }

    // 将 LocalDate 转换为 Excel 单元格数据（按目标格式输出）
    @Override
    public WriteCellData<?> convertToExcelData(
            LocalDate value,
            ExcelContentProperty contentProperty,
            GlobalConfiguration globalConfiguration
    ) {
        if (value == null) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(OUTPUT_FORMATTER.format(value));
    }
}