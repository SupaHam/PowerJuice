package com.supaham.powerjuice;

public class PJException extends Exception {

    /**
     * Create a new exception.
     */
    protected PJException() {
    }

    /**
     * Create a new exception with a message.
     *
     * @param message the message
     */
    public PJException(String message) {
        super(message);
    }

    /**
     * Create a new exception with a message and a cause.
     *
     * @param message the message
     * @param cause the cause
     */
    public PJException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create a new exception with a cause.
     *
     * @param cause the cause
     */
    public PJException(Throwable cause) {
        super(cause);
    }
}
