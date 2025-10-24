package net.lab1024.sa.admin.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

@Component
public class LocalDateConverter implements Converter<LocalDate> {

    private final DateTimeFormatter formatter;

    public LocalDateConverter() {
        this.formatter = new DateTimeFormatterBuilder()
                .appendPattern("[yyyy-MM-dd]")
                .appendPattern("[yyyy/MM/dd]")
                .appendPattern("[yyyy-M-d]")
                .appendPattern("[yyyy/M/d]")
                .appendPattern("[yyyy年MM月dd日]")
                .appendPattern("[yyyy年M月d日]")
                .toFormatter();
    }

    @Override
    public Class<LocalDate> supportJavaTypeKey() {
        return LocalDate.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public LocalDate convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();

        if (stringValue == null || stringValue.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(stringValue.trim(), formatter);
        } catch (DateTimeParseException e) {
            // 如果标准解析失败，尝试手动解析
            return parseManually(stringValue.trim());
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(LocalDate value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>("");
        }
        return new WriteCellData<>(value.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    private LocalDate parseManually(String dateStr) {
        // 移除所有非数字字符，只保留数字和分隔符
        String cleanStr = dateStr.replaceAll("[^0-9/\\-年月.]", "");

        // 用统一的分隔符替换各种分隔符
        String unified = cleanStr.replaceAll("[年月./]", "-");
        String[] parts = unified.split("-");

        if (parts.length == 3) {
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);
            return LocalDate.of(year, month, day);
        }

        throw new DateTimeParseException("无法解析日期: " + dateStr, dateStr, 0);
    }
}