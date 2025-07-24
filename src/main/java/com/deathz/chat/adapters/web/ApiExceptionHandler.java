package com.deathz.chat.adapters.web;

import java.time.OffsetDateTime;
import java.util.Map;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.deathz.chat.domain.exceptions.ConversationNotFoundException;
import com.deathz.chat.domain.exceptions.FailedToParseFileException;
import com.deathz.chat.domain.exceptions.FailedToParseRequestException;
import com.deathz.chat.domain.exceptions.FileNameIsNullException;
import com.deathz.chat.domain.exceptions.UnsupportedFileTypeException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<?> conversationNotFound(ConversationNotFoundException ex) {

        var body = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "NotFound",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(FailedToParseRequestException.class)
    public ResponseEntity<?> failedToParseRequest(FailedToParseRequestException ex) {

        var body = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "BadRequest",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(FailedToParseFileException.class)
    public ResponseEntity<?> failedToParseFile(FailedToParseFileException ex) {

        var body = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "error", "UnprocessableEntity",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(FileNameIsNullException.class)
    public ResponseEntity<?> fileNameIsNull(FileNameIsNullException ex) {

        var body = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "BadRequest",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    public ResponseEntity<?> unsupportedFileType(UnsupportedFileTypeException ex) {

        var body = Map.of(
                "timestamp", OffsetDateTime.now(),
                "status", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                "error", "UnsupportedMediaType",
                "message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(body);
    }
}
