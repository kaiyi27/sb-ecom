package com.ecommerce.project.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> myMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        Map<String, List<String>> response = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(err -> {
            String fieldName = err.getField();
            String message = err.getDefaultMessage();
            response.computeIfAbsent(fieldName, k -> new ArrayList<>())
                    .add(message);
        });

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
