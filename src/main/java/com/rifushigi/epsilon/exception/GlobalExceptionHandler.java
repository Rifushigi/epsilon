package com.rifushigi.epsilon.exception;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An internal server error occurred: " + ex.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred", ex);
        return new ErrorResponse(
                "BAD_REQUEST",
                ex.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument exception: {}", ex.getMessage());
        return new ErrorResponse(
                "INVALID_INPUT",
                ex.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("User not found: {}", ex.getMessage());
        return new ErrorResponse(
                "USER_NOT_FOUND",
                ex.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Validation failed: {}", errors);
        return new ErrorResponse(
                "VALIDATION_FAILED",
                "Validation failed for request",
                System.currentTimeMillis(),
                errors
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return new ErrorResponse(
                "METHOD_NOT_ALLOWED",
                ex.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation occurred", ex); // internal debug

        return new ErrorResponse(
                "DATA_INTEGRITY_ERROR",
                "Invalid or missing required data when saving URL.",
                System.currentTimeMillis()
        );
    }


    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException ex) {
        return new ErrorResponse(
                "ACCESS_DENIED",
                "Access denied: " + ex.getMessage(),
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed or missing request body: {}", ex.getMessage());
        return new ErrorResponse(
                "BAD_REQUEST",
                "Malformed or missing request body",
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(WeakKeyException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleWeakKeyException(WeakKeyException ex) {
        // Only log internally for developers/admins
        log.error("JWT weak key error detected: {}", ex.getMessage());

        // Send a generic message to the client
        return new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An internal error occurred while processing your request.",
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleJwtException(JwtException ex) {
        log.warn("JWT processing error: {}", ex.getMessage());
        return new ErrorResponse(
                "INVALID_TOKEN",
                "Invalid authentication token.",
                System.currentTimeMillis()
        );
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NoHandlerFoundException ex) {
        log.warn("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return new ErrorResponse(
                "NOT_FOUND",
                "The requested resource was not found",
                System.currentTimeMillis()
        );
    }

    @AllArgsConstructor
    @Data
    @NoArgsConstructor
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private long timestamp;
        private Map<String, String> details;

        public ErrorResponse(String errorCode, String message, long timestamp) {
            this.errorCode = errorCode;
            this.message = message;
            this.timestamp = timestamp;
        }
    }
}

