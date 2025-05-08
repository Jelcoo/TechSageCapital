package com.techsage.banking.exceptions;

public class TurnstileFailedException extends RuntimeException {
    public TurnstileFailedException() {
        super("Turnstile verification failed");
    }

    public TurnstileFailedException(String message) {
        super(message);
    }

    public TurnstileFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TurnstileFailedException(Throwable cause) {
        super(cause);
    }
}
