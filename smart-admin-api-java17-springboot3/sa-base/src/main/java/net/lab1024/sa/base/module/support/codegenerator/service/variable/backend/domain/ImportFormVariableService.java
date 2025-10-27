//package net.lab1024.sa.base.module.support.codegenerator.service.variable.backend.domain;
//
//import net.lab1024.sa.base.module.support.codegenerator.domain.form.CodeGeneratorConfigForm;
//import net.lab1024.sa.base.module.support.codegenerator.domain.model.CodeField;
//import net.lab1024.sa.base.module.support.codegenerator.domain.model.CodeInsertAndUpdate;
//import net.lab1024.sa.base.module.support.codegenerator.domain.model.CodeInsertAndUpdateField;
//import net.lab1024.sa.base.module.support.codegenerator.service.variable.CodeGenerateBaseVariableService;
//import org.apache.commons.lang3.tuple.ImmutablePair;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class ImportFormVariableService extends CodeGenerateBaseVariableService {
//
//    @Override
//    public boolean isSupport(CodeGeneratorConfigForm form) {
//        return true;
//    }
//
//    @Override
//    public Map<String, Object> getInjectVariablesMap(CodeGeneratorConfigForm form) {
//        Map<String, Object> variablesMap = new HashMap<>();
//
//        Map<String, CodeField> fieldMap = getFieldMap(form);
//        List<CodeInsertAndUpdateField> importFieldList = form.getInsertAndUpdate().getFieldList().stream().filter(e -> {
//                    boolean isUpdate = Boolean.TRUE.equals(e.getUpdateFlag());
//                    CodeField codeField = fieldMap.get(e.getColumnName());
//                    if (codeField == null) {
//                        return false;
//                    }
//
//                    if (Boolean.TRUE.equals(codeField.getPrimaryKeyFlag())) {
//                        e.setRequiredFlag(true);
//                    }
//
//                    return isUpdate || Boolean.TRUE.equals(codeField.getPrimaryKeyFlag());
//                }
//
//        ).collect(Collectors.toList());
//
//        ImmutablePair<List<String>, List<Map<String, Object>>> packageListAndFields = getPackageListAndFields(updateFieldList, form);
//
//        variablesMap.put("packageName", form.getBasic().getJavaPackageName() + ".domain.form");
//        variablesMap.put("importPackageList", packageListAndFields.getLeft());
//        variablesMap.put("fields", packageListAndFields.getRight());
//
//        return variablesMap;
//    }
//
//
//}
