package br.com.jackson.stop.sala;

import java.util.List;

public class SalaFactory {
  public static final String CATEGORIA_1 = "Categoria 1";

  public static NovaSalaRequest criaNovaSalaRequest(
      String senha, int rodadas, List<String> letras) {
    return new NovaSalaRequest(rodadas, 10, senha, List.of(CATEGORIA_1), letras, TempoJogo.MEDIO);
  }

  public static Sala criaSalaSemSenha() {
    return new Sala(2, 10, List.of(new Categoria(CATEGORIA_1)), List.of("A", "B"), TempoJogo.MEDIO);
  }
}
