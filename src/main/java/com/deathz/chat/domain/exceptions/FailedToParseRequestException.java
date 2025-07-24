package com.deathz.chat.domain.exceptions;

public class FailedToParseRequestException extends RuntimeException {

    public FailedToParseRequestException(Throwable cause) {

        super("Failed to parse request", cause);
    }

    public FailedToParseRequestException(String message) {

        super(message);
    }

}
