package com.tailorly.tailorly_backend.exception;

public class DocxGenerationException extends RuntimeException {

    public DocxGenerationException(String message) {
        super(message);
    }

    public DocxGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
