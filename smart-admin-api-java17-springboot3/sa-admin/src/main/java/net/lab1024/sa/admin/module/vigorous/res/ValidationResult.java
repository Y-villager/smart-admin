package net.lab1024.sa.admin.module.vigorous.res;

import com.alibaba.excel.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 验证结果封装类（优化版）
 */
public class ValidationResult {
    private final List<String> errorMsg = new ArrayList<>();  // 支持多个错误信息
    private final List<String> remindMsg = new ArrayList<>(); // 支持多个提醒信息

    // 添加错误信息（支持链式调用）
    public ValidationResult addError(String errorMsg) {
        if (StringUtils.isNotBlank(errorMsg)) {
            this.errorMsg.add(errorMsg);
        }
        return this;
    }

    // 添加提醒信息（支持链式调用）
    public ValidationResult addRemind(String remindMsg) {
        if (StringUtils.isNotBlank(remindMsg)) {
            this.remindMsg.add(remindMsg);
        }
        return this;
    }

    // 判断是否存在错误
    public boolean hasErrors() {
        return !errorMsg.isEmpty();
    }

    // 判断是否存在提醒
    public boolean hasReminds() {
        return !remindMsg.isEmpty();
    }

    // 获取格式化后的错误信息
    public String getErrorMsg() {
        return String.join("；", errorMsg);
    }

    // 获取格式化后的提醒信息
    public String getRemindMsg() {
        return String.join("；", remindMsg);
    }

    public String getAllMsg() {
        StringBuilder builder = new StringBuilder();
        if (hasErrors()){
            builder.append(errorMsg);
        }
        if (hasReminds()){
            builder.append(remindMsg);
        }
        return builder.toString();
    }

}