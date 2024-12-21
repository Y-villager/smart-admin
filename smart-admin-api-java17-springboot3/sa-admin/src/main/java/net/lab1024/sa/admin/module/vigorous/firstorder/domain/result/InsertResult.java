package net.lab1024.sa.admin.module.vigorous.firstorder.domain.result;

public class InsertResult {

    // 是否操作成功
    private boolean success;
    // 操作信息，例如错误信息
    private String message;
    // 影响的行数
    private int affectedRows;
    // 其他附加数据（如果需要的话，可以存放特定的返回值或者对象）
    private Object data;

    // 默认构造函数
    public InsertResult() {}

    // 带有 success 和 message 的构造函数
    public InsertResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // 带有 success, message 和 affectedRows 的构造函数
    public InsertResult(boolean success, String message, int affectedRows) {
        this.success = success;
        this.message = message;
        this.affectedRows = affectedRows;
    }

    // 带有 success, message 和 data 的构造函数
    public InsertResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // 带有 success, message, affectedRows 和 data 的构造函数
    public InsertResult(boolean success, String message, int affectedRows, Object data) {
        this.success = success;
        this.message = message;
        this.affectedRows = affectedRows;
        this.data = data;
    }

    // Getter 和 Setter 方法

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(int affectedRows) {
        this.affectedRows = affectedRows;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "InsertResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", affectedRows=" + affectedRows +
                ", data=" + data +
                '}';
    }
}
