package br.com.jackson.stop.sala;

public class CategoriaResponse {
  private final String nomeCategoria;

  public CategoriaResponse(Categoria categoria) {
    this.nomeCategoria = categoria.getNome();
  }

  public String getNomeCategoria() {
    return nomeCategoria;
  }
}
