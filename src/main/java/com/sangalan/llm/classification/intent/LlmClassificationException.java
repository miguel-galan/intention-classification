package com.sangalan.llm.classification.intent;

public class LlmClassificationException extends RuntimeException {

    public LlmClassificationException(String message) {
        super(message);
    }

    public LlmClassificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
