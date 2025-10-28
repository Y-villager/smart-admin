package net.lab1024.sa.admin.module.vigorous.receivables.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.lab1024.sa.admin.module.vigorous.receivables.domain.form.ReceivablesImportForm;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FailedData {
    private Integer rowIndex;
    private ReceivablesImportForm data;
    private String errorMessage;

    public ReceivablesImportForm toReceivablesImportForm() {
        data.setErrorMsg(errorMessage);
        return data;
    }
}