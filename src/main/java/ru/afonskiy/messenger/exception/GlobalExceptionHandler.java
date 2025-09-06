package ru.afonskiy.messenger.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)

    public ResponseEntity<String> handleIllegalArgumentException(String errorMessage) {
        return ResponseEntity.badRequest().body(errorMessage);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RuntimeException> handleRuntimeException(RuntimeException error) {
        return ResponseEntity.badRequest().body(error);
    }
}
