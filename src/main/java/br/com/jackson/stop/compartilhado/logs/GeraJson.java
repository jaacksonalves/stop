package br.com.jackson.stop.compartilhado.logs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GeraJson {

  private static final ObjectMapper mapper = new ObjectMapper();

  public GeraJson() {
    throw new IllegalStateException("Classe de utilidade");
  }

  public static String json(Object object) {
    try {
      return mapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
