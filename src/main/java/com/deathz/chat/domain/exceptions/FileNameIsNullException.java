package com.deathz.chat.domain.exceptions;

public class FileNameIsNullException extends RuntimeException {

    public FileNameIsNullException() {

        super("File name cannot be null");
    }

    public FileNameIsNullException(String message) {

        super(message);
    }

    public FileNameIsNullException(String message, Throwable cause) {

        super(message, cause);
    }

    public FileNameIsNullException(Throwable cause) {

        super("File name cannot be null", cause);
    }

}
