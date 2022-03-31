package br.com.jackson.stop.compartilhado.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExceptionsHandlerResponse {

  private String mensagem;
  private final String ocorridoEm =
      LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

  public ExceptionsHandlerResponse(String mensagem) {
    this.mensagem = mensagem;
  }

  public ExceptionsHandlerResponse() {}

  public String getMensagem() {
    return mensagem;
  }

  public String getOcorridoEm() {
    return ocorridoEm;
  }
}
