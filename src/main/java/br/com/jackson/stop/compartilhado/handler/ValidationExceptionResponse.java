package br.com.jackson.stop.compartilhado.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ValidationExceptionResponse {

  private Map<String, List<String>> erros;
  private final String ocorridoEm =
      LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

  public ValidationExceptionResponse(Map<String, List<String>> erros) {
    this.erros = erros;
  }

  public ValidationExceptionResponse() {}

  public Map<String, List<String>> getErros() {
    return erros;
  }

  public String getOcorridoEm() {
    return ocorridoEm;
  }

}
