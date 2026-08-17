package dev.htmlib.markup;

public final class MarkupException extends RuntimeException {

    public MarkupException(String message) {
        super(message);
    }

    public MarkupException(String message, Throwable cause) {
        super(message, cause);
    }
}
