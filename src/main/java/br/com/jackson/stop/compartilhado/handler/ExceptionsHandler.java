package br.com.jackson.stop.compartilhado.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class ExceptionsHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(BAD_REQUEST)
  public ValidationExceptionResponse methodArgumentNotValidExceptionHandler(
      MethodArgumentNotValidException ex) {

    var map = new HashMap<String, List<String>>();

    if (ex.getBindingResult().getFieldErrors().isEmpty()) {
      var list = new ArrayList<String>();
      list.add(ex.getBindingResult().getGlobalErrors().iterator().next().getDefaultMessage());
      map.put("Erro na requisição", list);
      return new ValidationExceptionResponse(map);
    }

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            erro -> {
              if (map.containsKey(erro.getField())) {
                map.get(erro.getField()).add(erro.getDefaultMessage());
              } else {
                var list = new ArrayList<String>();
                list.add(erro.getDefaultMessage());
                map.put(erro.getField(), list);
              }
            });

    return new ValidationExceptionResponse(map);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<String> responseStatusExceptionHandler(ResponseStatusException ex) {
    return ResponseEntity.status(ex.getStatus()).body(ex.getReason());
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public String httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException exception) {
    var invalidFormat = (InvalidFormatException) exception.getCause();

    return invalidFormat.getValue() + " não é um valor válido";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(BAD_REQUEST)
  public String exceptionHandler(Exception ex) {
    return ex.getMessage();
  }
}
