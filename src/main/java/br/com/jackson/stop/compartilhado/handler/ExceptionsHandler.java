package br.com.jackson.stop.compartilhado.handler;

import br.com.jackson.stop.compartilhado.logs.LogBuilder;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger log = LoggerFactory.getLogger(ExceptionsHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(BAD_REQUEST)
  public ValidationExceptionResponse methodArgumentNotValidExceptionHandler(
      MethodArgumentNotValidException ex) {
    var erros = new HashMap<String, List<String>>();

    var listaErrosGlobais = new ArrayList<String>();
    ex.getBindingResult()
        .getGlobalErrors()
        .forEach(e -> listaErrosGlobais.add(e.getDefaultMessage()));
    erros.put("Erro na requisição", listaErrosGlobais);

    ex.getBindingResult()
        .getFieldErrors()
        .forEach(
            erro -> {
              if (erros.containsKey(erro.getField())) {
                erros.get(erro.getField()).add(erro.getDefaultMessage());
              } else {
                var listaErrosDeCampos = new ArrayList<String>();
                listaErrosDeCampos.add(erro.getDefaultMessage());
                erros.put(erro.getField(), listaErrosDeCampos);
              }
            });

    return new ValidationExceptionResponse(erros);
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ExceptionsHandlerResponse> responseStatusExceptionHandler(
      ResponseStatusException ex) {

    return ResponseEntity.status(ex.getStatus())
        .body(new ExceptionsHandlerResponse(ex.getReason()));
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ExceptionsHandlerResponse httpMessageNotReadableExceptionHandler(
      HttpMessageNotReadableException ex) {
    var invalidFormat = (InvalidFormatException) ex.getCause();

    return new ExceptionsHandlerResponse(invalidFormat.getValue() + " não é um valor válido");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(BAD_REQUEST)
  public ExceptionsHandlerResponse exceptionHandler(Exception ex) {

    LogBuilder.metodo()
        .oQueEstaAcontecendo("Capturando Exception no handler de Exception genérica")
        .adicionaInformacao("mensagem: ", ex.getMessage())
        .erro(log, ex);

    return new ExceptionsHandlerResponse(ex.getLocalizedMessage());
  }
}
