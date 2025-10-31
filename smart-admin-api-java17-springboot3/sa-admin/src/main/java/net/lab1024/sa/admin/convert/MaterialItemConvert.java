//package net.lab1024.sa.admin.convert;
//
//import com.alibaba.excel.converters.Converter;
//import com.alibaba.excel.enums.CellDataTypeEnum;
//import com.alibaba.excel.metadata.GlobalConfiguration;
//import com.alibaba.excel.metadata.data.CellData;
//import com.alibaba.excel.metadata.property.ExcelContentProperty;
//import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesDetailsEntity;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Component
//public class MaterialItemsConverter implements Converter<List<ReceivablesDetailsEntity>> {
//
//    @Override
//    public Class<?> supportJavaTypeKey() {
//        return List.class;
//    }
//
//    @Override
//    public CellDataTypeEnum supportExcelTypeKey() {
//        return CellDataTypeEnum.STRING;
//    }
//
//    @Override
//    public List<ReceivablesDetailsEntity> convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
//        // 导入时转换，根据需求实现
//        return new ArrayList<>();
//    }
//
//    @Override
//    public CellData<String> convertToExcelData(List<ReceivablesDetailsEntity> value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
//        if (value == null || value.isEmpty()) {
//            return new CellData<>("");
//        }
//
//        // 将物料明细列表转换为字符串，用换行符分隔
//        String materialInfo = value.stream()
//                .map(this::formatMaterialItem)
//                .collect(Collectors.joining("\n"));
//
//        return new CellData<>(materialInfo);
//    }
//
//    private String formatMaterialItem(ReceivablesDetailsEntity item) {
//        return String.format("%s %s %s %s %s %s",
//                item.getMaterialName() != null ? item.getMaterialName() : "",
//                item.getMaterialSpec() != null ? item.getMaterialSpec() : "",
//                item.getUnit() != null ? item.getUnit() : "",
//                item.getQuantity() != null ? item.getQuantity().toString() : "0",
//                item.getPrice() != null ? item.getPrice().toString() : "0");
//    }
//}