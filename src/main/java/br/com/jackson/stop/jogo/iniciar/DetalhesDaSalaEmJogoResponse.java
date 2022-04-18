package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.sala.CategoriaResponse;
import br.com.jackson.stop.sala.Sala;
import br.com.jackson.stop.usuario.UsuarioResponse;

import java.util.List;

public class DetalhesDaSalaEmJogoResponse {
  private final List<UsuarioResponse> usuarios;
  private final List<String> letras;
  private final int rodadas;
  private final List<CategoriaResponse> categorias;

  public DetalhesDaSalaEmJogoResponse(Sala sala) {
    this.usuarios = sala.getUsuarios().stream().map(UsuarioResponse::new).toList();
    this.letras = sala.getLetras();
    this.rodadas = sala.getInformacoesSala().getRodadas();
    this.categorias = sala.getCategorias().stream().map(CategoriaResponse::new).toList();
  }

  public List<UsuarioResponse> getUsuarios() {
    return usuarios;
  }

  public List<String> getLetras() {
    return letras;
  }

  public int getRodadas() {
    return rodadas;
  }

  public List<CategoriaResponse> getCategorias() {
    return categorias;
  }
}
