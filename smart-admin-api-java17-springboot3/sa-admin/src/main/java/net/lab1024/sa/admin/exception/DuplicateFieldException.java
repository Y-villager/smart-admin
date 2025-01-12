package net.lab1024.sa.admin.exception;

public class DuplicateFieldException extends RuntimeException {
    private String name;
    private String value;

    public DuplicateFieldException(String name, String value) {
        super(String.format("'%s' in class '%s'", name, value));
    }
}
