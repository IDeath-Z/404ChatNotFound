package com.deathz.chat.domain.exceptions;

public class UnsupportedFileTypeException extends RuntimeException {

    public UnsupportedFileTypeException() {

        super("Unsupported file type");
    }

    public UnsupportedFileTypeException(String message, Throwable cause) {

        super(message, cause);
    }

    public UnsupportedFileTypeException(Throwable cause) {

        super("Unsupported file type", cause);
    }

}
