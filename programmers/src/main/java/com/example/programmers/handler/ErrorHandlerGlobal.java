package com.example.programmers.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandlerGlobal {

    /**
     * Trata erros de validação dos campos da requisição (@Valid).
     * É acionado quando alguma regra de Bean Validation falha.
    */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Trata erros de leitura do JSON da requisição.
     * Ocorre quando o corpo da requisição está mal formatado,
     * ou quando um valor não pode ser convertido (ex: enum inválido).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidEnum(HttpMessageNotReadableException ex) {

        String message = "Invalid request body";
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException ife) {
            String field = ife.getPath().get(0).getFieldName();
            String invalidValue = ife.getValue().toString();
            message = "Invalid value '" + invalidValue + "' for field '" + field + "'";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", message));
    }
}