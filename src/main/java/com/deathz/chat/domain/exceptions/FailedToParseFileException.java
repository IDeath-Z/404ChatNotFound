package com.deathz.chat.domain.exceptions;

public class FailedToParseFileException extends RuntimeException {

    public FailedToParseFileException(String fileName) {

        super("Failed to parse file: " + fileName);
    }

    public FailedToParseFileException(String message, Throwable cause) {

        super(message, cause);
    }

    public FailedToParseFileException(Throwable cause) {

        super("Failed to parse file", cause);
    }

}
