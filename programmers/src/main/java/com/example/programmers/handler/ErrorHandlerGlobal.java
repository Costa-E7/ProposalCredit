package com.example.programmers.handler;

import com.example.programmers.exception.InvalidProposalStateException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandlerGlobal {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandlerGlobal.class);

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
        log.error("HttpMessageNotReadableException: {}", ex.getMessage(), ex);

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


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrity(
            DataIntegrityViolationException ex
    ) {
        log.error("DataIntegrityViolationException: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of(
                                "error",
                                "Erro de integridade dos dados, Não foi possível salvar os dados."
                        )
                );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseError(
            DataAccessException ex
    ) {
        log.error("DataAccessException: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of(
                                "error",
                                "Ocorreu um erro ao acessar o banco de dados."
                        )
                );
    }

    @ExceptionHandler(InvalidProposalStateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidProposalState(
            InvalidProposalStateException ex
    ) {
        log.error("InvalidProposalStateException: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of(
                                "error",
                                ex.getMessage()
                        )
                );
    }
}
