package br.com.jackson.stop.sala;

public class CategoriasResponse {
  private final String nomeCategoria;

  public CategoriasResponse(Categoria categoria) {
    this.nomeCategoria = categoria.getNome();
  }

    public String getNomeCategoria() {
        return nomeCategoria;
    }
}
