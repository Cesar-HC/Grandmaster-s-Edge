package com.upc.ajedrezbackend.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        log.error("Error occurred", ex);
        ErrorResponse error = new ErrorResponse(ex.getStatusCode().value(), ex.getReason());
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("Error occurred", ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Recurso no encontrado");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ErrorResponse> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex) {
        log.error("Error occurred", ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Recurso no encontrado");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Error occurred", ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidaciones(MethodArgumentNotValidException ex) {
        List<Map<String, String>> listaErrores = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, String> detalle = new HashMap<>();
            detalle.put("campo", error.getField());
            detalle.put("mensaje", error.getDefaultMessage());
            listaErrores.add(detalle);
        });
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Errores de validación", listaErrores
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        log.error("Error occurred", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "No es posible atender la petición, vuelva a intentarlo"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception occurred", ex);
        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
