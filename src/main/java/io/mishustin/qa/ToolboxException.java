package io.mishustin.qa;

public class ToolboxException extends RuntimeException {

    public ToolboxException(Throwable e) {
        super(e);
    }

    public ToolboxException(String e) {
        super(e);
    }
}
