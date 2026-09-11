package com.splitease.exception;

import com.splitease.dto.ErrorReponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    //404- Not found Exception
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorReponse> handleNotFound(NotFoundException ex, HttpServletRequest req){
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }
    //400 - bad request from client
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorReponse> handleBadRequest(BadRequestException ex, HttpServletRequest req){
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    //400-@valid failures(Eg:blank name, invalid email)-with per field messages
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorReponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req){
        Map<String, String> fieldErrors=new HashMap<>();
        for(FieldError fe: ex.getBindingResult().getFieldErrors()){
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        ErrorReponse body=base(HttpStatus.BAD_REQUEST, "Validation failed", req);
        body.setFieldErrors(fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    //409- DB Constraint violation(eg: adding the same member twice)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorReponse> handleConflict(DataIntegrityViolationException ex, HttpServletRequest req){
        return build(HttpStatus.CONFLICT, "this action conflicts with an existing record(duplicate or constraint)", req);
    }

    //500- internal server error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorReponse> handleGeneric(Exception ex, HttpServletRequest req){
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occured", req);
    }

    private ResponseEntity<ErrorReponse> build(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(base(status, message, req));
    }

    private ErrorReponse base(HttpStatus status, String message, HttpServletRequest req) {
        ErrorReponse r=new ErrorReponse();
        r.setTimestamp(Instant.now());
        r.setStatus(status.value());
        r.setError(status.getReasonPhrase());
        r.setMessage(message);
        r.setPath(req.getRequestURI());
        return r;
    }
}
