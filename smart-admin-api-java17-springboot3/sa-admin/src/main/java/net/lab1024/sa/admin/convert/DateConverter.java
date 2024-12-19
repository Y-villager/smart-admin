package net.lab1024.sa.admin.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateConverter implements Converter<Date> {

    SimpleDateFormat[] formats = {
            new SimpleDateFormat("yyyy/MM/dd"),
            new SimpleDateFormat("yyyy-MM-dd"),
            new SimpleDateFormat("yyyy.MM.dd"),
            new SimpleDateFormat("yyyyMMdd")
    };

    @Override
    public Class<Date> supportJavaTypeKey() {
        return Date.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING; // Excel 列数据类型为字符串类型
    }

    @Override
    public Date convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                  GlobalConfiguration globalConfiguration) throws Exception {

        if (cellData.getType().equals(CellDataTypeEnum.NUMBER)) {
            long cellValue = cellData.getNumberValue().longValue();
            try {
                // 如果是数字日期（如 yyyyMMdd），尝试解析
                if (cellValue > 19900100) {
                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
                    return originalFormat.parse(String.valueOf(cellValue));
                }
            } catch (ParseException e) {
                log.warn("exception when parse numerical time with format yyyyMMdd", e);
            }
        } else if (cellData.getType().equals(CellDataTypeEnum.STRING)) {
            Date date = null;
            String cellValue = cellData.getStringValue();
            // 依次尝试不同的格式进行解析
            for (SimpleDateFormat format : formats) {
                try {
                    date = format.parse(cellValue);
                    if (date != null) {
                        return date;
                    }
                } catch (ParseException e) {
                    log.warn("parse {} exception with format:{}", cellValue, format);
                }
            }
        }
        log.warn("cannot parse the date format from {}", cellData.getStringValue());
        return null;
    }
}
