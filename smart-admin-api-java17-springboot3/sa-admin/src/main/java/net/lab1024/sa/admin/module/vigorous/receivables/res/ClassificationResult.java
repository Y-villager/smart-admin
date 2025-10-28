package net.lab1024.sa.admin.module.vigorous.receivables.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.entity.ReceivablesEntity;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesImportForm;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClassificationResult {
    // 有效的表单数据
    private List<ReceivablesImportForm> validDataList = new ArrayList<>();

    // 失败的数据（包含错误信息）
    private List<FailedData> failedDataList = new ArrayList<>();

    // 转换后的应收单实体
    private List<ReceivablesEntity> receivablesEntities = new ArrayList<>();

    public void addValidData(ReceivablesImportForm form) {
        this.validDataList.add(form);
    }

    public void addFailedData(FailedData failedData) {
        this.failedDataList.add(failedData);
    }

    public void addReceivablesEntity(ReceivablesEntity entity) {
        this.receivablesEntities.add(entity);
    }

    public int getTotalCount() {
        return validDataList.size() + failedDataList.size();
    }

    public int getSuccessCount() {
        return receivablesEntities.size();
    }

    public int getFailedCount() {
        return failedDataList.size();
    }
}

