package br.com.jackson.stop.usuario;

public class UsuarioResponse {

  private final String nome;
  private final Integer nota;

  public UsuarioResponse(Usuario usuario) {
    this.nome = usuario.getNome();
    this.nota = usuario.getNota();
  }

  public String getNome() {
    return nome;
  }

  public Integer getNota() {
    return nota;
  }
}
