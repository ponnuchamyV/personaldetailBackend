package com.example.ivetestcase.exception.handler;

import com.example.ivetestcase.exception.DuplicateResourceException;
import com.example.ivetestcase.exception.ProfileNotFoundException;
import com.example.ivetestcase.response.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ===================== Profile Not Found =====================
    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<CommonResponse<?>> handleProfileNotFound(ProfileNotFoundException ex) {
        logger.warn("Profile not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponse.failure(ex.getMessage()));
    }

    // ===================== Duplicate Resource =====================
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<CommonResponse<?>> handleDuplicateResource(DuplicateResourceException ex) {
        logger.warn("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponse.failure(ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<Map<String, String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        // Pass the errors map as data
        return ResponseEntity.badRequest()
                .body(CommonResponse.failure("Validation failed", errors));
    }



    // ===================== Malformed JSON / Invalid Enum =====================
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        logger.warn("Malformed JSON request: {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(CommonResponse.failure("Malformed JSON request or invalid enum value."));
    }

    // ===================== Generic Exception =====================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<?>> handleGenericException(Exception ex) {
        logger.error("Unhandled exception occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponse.failure("An unexpected error occurred. Please try again later."));
    }
}
