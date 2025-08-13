package com.ufrn.imd.diretoriadeprojetos.advices;

import com.ufrn.imd.diretoriadeprojetos.errors.HttpError;
import com.ufrn.imd.diretoriadeprojetos.errors.MethodNotAllowed;
import com.ufrn.imd.diretoriadeprojetos.errors.RouteNotFound;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(HttpError.class)
    public ResponseEntity<Map<String, Object>> handleHttpErrors(HttpError error) {
        return ResponseEntity
                .status(error.getStatus())
                .body(error.getError());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleAnnotationValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, List<String>> fieldErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
        });

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("message", "Os dados fornecidos são inválidos.");
        responseBody.put("errors", fieldErrors);

        return new ResponseEntity<>(responseBody, HttpStatus.UNPROCESSABLE_ENTITY); // Status 422
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleHandlerNotFound() {
        RouteNotFound error = new RouteNotFound();
        return ResponseEntity
                .status(error.getStatus())
                .body(error.getError());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHandlerMethodNotAllowed() {
        MethodNotAllowed error = new MethodNotAllowed();
        return ResponseEntity
                .status(error.getStatus())
                .body(error.getError());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.BAD_REQUEST.value());
        responseBody.put("error", "Requisição Inválida");
        responseBody.put("message", ex.getMessage());

        return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", HttpStatus.FORBIDDEN.value());
        responseBody.put("error", "Acesso Negado");
        responseBody.put("message", "Você não tem permissão para realizar esta ação.");

        return new ResponseEntity<>(responseBody, HttpStatus.FORBIDDEN);
    }

}