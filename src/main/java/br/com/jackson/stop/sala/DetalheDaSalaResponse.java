package br.com.jackson.stop.sala;

import java.util.List;

public class DetalheDaSalaResponse {
  private final TempoJogo tempoJogo;
  private final List<CategoriaResponse> categorias;
  private final List<String> letras;

  public DetalheDaSalaResponse(Sala sala) {
    this.tempoJogo = sala.getInformacoesSala().getTempoJogo();
    this.categorias = sala.getCategorias().stream().map(CategoriaResponse::new).toList();
    this.letras = sala.getLetras();
  }

  public TempoJogo getTempoJogo() {
    return tempoJogo;
  }

  public List<CategoriaResponse> getCategorias() {
    return categorias;
  }

  public List<String> getLetras() {
    return letras;
  }
}
