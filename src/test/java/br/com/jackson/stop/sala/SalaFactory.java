package br.com.jackson.stop.sala;

import br.com.jackson.stop.jogo.EntrarNoJogoRequest;
import br.com.jackson.stop.usuario.Usuario;

import java.util.List;

public class SalaFactory {
  public static final String CATEGORIA_1 = "Categoria 1";
  public static final String CATEGORIA_2 = "Categoria 2";
  public static final String CATEGORIA_3 = "Categoria 3";

  public static NovaSalaRequest criaNovaSalaRequest(
      String senha, int rodadas, List<String> letras) {
    return new NovaSalaRequest(rodadas, 10, senha, List.of(CATEGORIA_1), letras, TempoJogo.MEDIO);
  }

  public static Sala criaSalaSemSenha() {
    return new Sala(
        4, 4, List.of(new Categoria(CATEGORIA_1)), List.of("A", "B", "C", "D"), TempoJogo.MEDIO);
  }

  public static Sala criaSalaComSenha() {
    var sala =
        new Sala(
            4,
            4,
            List.of(new Categoria(CATEGORIA_2)),
            List.of("A", "B", "C", "D"),
            TempoJogo.MEDIO);
    sala.adicionaSenha("senha");
    return sala;
  }

  public static Sala criaSalaComSenha2() {
    var sala =
        new Sala(
            4,
            4,
            List.of(new Categoria(CATEGORIA_3)),
            List.of("A", "B", "C", "D"),
            TempoJogo.MEDIO);
    sala.adicionaSenha("senha");
    return sala;
  }

  public static Usuario criaUsuario() {
    return new Usuario("usuario");
  }

  public static EntrarNoJogoRequest criaEntrarNoJogoRequest(String senha) {
    return new EntrarNoJogoRequest("nome", senha);
  }
}
