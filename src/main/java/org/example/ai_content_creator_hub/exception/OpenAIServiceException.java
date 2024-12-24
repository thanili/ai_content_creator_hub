package org.example.ai_content_creator_hub.exception;

public class OpenAIServiceException extends RuntimeException {
    public OpenAIServiceException(String message) {
        super(message);
    }

    public OpenAIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
