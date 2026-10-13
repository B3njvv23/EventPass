package com.preventa.ordenes.exception;

import java.util.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(ApiException.class)
  ResponseEntity<ProblemDetail> domain(ApiException ex) {
    return problem(ex.status, ex.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ProblemDetail> validation(MethodArgumentNotValidException ex) {
    ProblemDetail p = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Datos invalidos");
    Map<String, String> errors = new LinkedHashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
    p.setProperty("errors", errors);
    return ResponseEntity.badRequest().body(p);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<ProblemDetail> duplicate(DataIntegrityViolationException ex) {
    return problem(HttpStatus.CONFLICT, "Datos duplicados o recurso relacionado");
  }

  @ExceptionHandler({
    org.springframework.http.converter.HttpMessageNotReadableException.class,
    org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class
  })
  ResponseEntity<ProblemDetail> malformed(Exception ex) {
    return problem(HttpStatus.BAD_REQUEST, "Formato de solicitud invalido");
  }

  @ExceptionHandler(org.springframework.web.client.RestClientException.class)
  ResponseEntity<ProblemDetail> upstream(Exception ex) {
    return problem(
        HttpStatus.BAD_GATEWAY,
        "No fue posible completar la comunicacion con otro microservicio; reintenta la operacion");
  }

  private ResponseEntity<ProblemDetail> problem(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, message));
  }
}
