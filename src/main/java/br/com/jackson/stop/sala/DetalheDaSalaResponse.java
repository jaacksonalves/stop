package br.com.jackson.stop.sala;

import java.util.List;

public class DetalheDaSalaResponse {
  private final TempoJogo tempoJogo;
  private final List<CategoriasResponse> categorias;
  private final List<String> letras;

  public DetalheDaSalaResponse(Sala sala) {
    this.tempoJogo = sala.getTempoJogo();
    this.categorias = sala.getCategorias().stream().map(CategoriasResponse::new).toList();
    this.letras = sala.getLetras();
  }

  public TempoJogo getTempoJogo() {
    return tempoJogo;
  }

  public List<CategoriasResponse> getCategorias() {
    return categorias;
  }

  public List<String> getLetras() {
    return letras;
  }
}
