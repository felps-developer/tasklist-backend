/*
 * @(#)GlobalExceptionHandler.java
 *
 * Copyright (c) J-Tech Solucoes em Informatica.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of J-Tech.
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with J-Tech.
 */
package br.com.jtech.tasklist.config.infra.handlers;

import br.com.jtech.tasklist.config.infra.exceptions.*;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Create a global exception handler for intercepting all exceptions in the api.
 *
 * @author jtech
 * class GlobalExceptionHandler
 **/
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Hidden
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage("Erro na validação dos dados");
        error.setTimestamp(LocalDateTime.now());
        error.setSubErrors(subErrors(ex));
        error.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(error);
    }

    @Hidden
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST);
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        error.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(error);
    }

    @Hidden
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiError error = new ApiError(HttpStatus.NOT_FOUND);
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        error.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(error);
    }

    @Hidden
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorizedException(UnauthorizedException ex) {
        ApiError error = new ApiError(HttpStatus.FORBIDDEN);
        error.setMessage(ex.getMessage());
        error.setTimestamp(LocalDateTime.now());
        error.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(error);
    }

    @Hidden
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentialsException(BadCredentialsException ex) {
        ApiError error = new ApiError(HttpStatus.UNAUTHORIZED);
        error.setMessage("Credenciais inválidas");
        error.setTimestamp(LocalDateTime.now());
        error.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(error);
    }

    @Hidden
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR);
        error.setMessage("Erro interno do servidor");
        error.setTimestamp(LocalDateTime.now());
        error.setDebugMessage(ex.getLocalizedMessage());
        return buildResponseEntity(error);
    }

    private ResponseEntity<ApiError> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private List<ApiSubError> subErrors(MethodArgumentNotValidException ex) {
        List<ApiSubError> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            ApiValidationError api = new ApiValidationError(
                ex.getObjectName(), 
                fieldError.getField(), 
                fieldError.getRejectedValue(), 
                fieldError.getDefaultMessage()
            );
            errors.add(api);
        }
        return errors;
    }
}

