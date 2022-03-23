package br.com.jackson.stop.sala;

import javax.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

// ICP(8.0)
public class NovaSalaRequest {

  // 0.5
  @NotNull
  @Positive
  @Max(12)
  public final int rodadas;

  // 0.5
  @NotNull
  @Positive
  @Max(12)
  public final int maximoJogadores;

  // 0.5
  public final String senha;

  // 0.5
  @NotNull @NotEmpty public final List<String> categorias;

  // 0.5
  @NotNull @NotEmpty
  public final List<
          @Pattern(regexp = "^[A-Z]", message = "Apenas são permitidas letras de A a Z") String>
      letras;

  // 0.5
  @NotNull public final TempoJogo tempoJogo;

  public NovaSalaRequest(
      int rodadas,
      int maximoJogadores,
      String senha,
      List<String> categorias,
      List<String> letras,
      TempoJogo tempoJogo) {
    validaLetrasCompativelRodadas(letras, rodadas);

    this.rodadas = rodadas;
    this.maximoJogadores = maximoJogadores;
    this.senha = senha;
    this.categorias = categorias;
    this.letras = letras;
    this.tempoJogo = tempoJogo;
  }

  /** A quantidade de letras deve ser igual ao número de rodadas. */
  private void validaLetrasCompativelRodadas(List<String> letras, int rodadas) {
    //1
    if (letras.size() < rodadas) {
      throw new IllegalArgumentException(
          "A quantidade de letras deve ser maior ou igual ao número de rodadas");
    }
  }

  // #nãoSeiExplicar: jackson - por algum motivo o @Pattern só funciona se eu deixar esse getter
  // aqui
  public List<String> getLetras() {
    return letras;
  }

  // 1
  public Sala toSala(CategoriaRepository categoriaRepository) {
    var listaCategorias = new ArrayList<Categoria>();

    // ps.: não gostei muito dessa solução. Tentei fazer com stram().map(), mas não funcionou,
    // aparentemente o stream ou o map estava fazendo as coisas paralelas, mesmo sem usar o
    // parallelstream. Preciso investigar mais. tinha feito assim:
    // var listaCategorias =
    //        this.categorias.stream()
    //            .map(
    //                nomeCategoria ->
    //                    categoriaRepository
    //                        .findByNome(nomeCategoria)
    //                        .orElse(categoriaRepository.save(new Categoria(nomeCategoria))))
    //            .toList();

    this.categorias.forEach(
        nomeCategoria -> {
          // 1
          if (!categoriaRepository.existsByNome(nomeCategoria)) {
            listaCategorias.add(categoriaRepository.save(new Categoria(nomeCategoria)));
            // 1
          } else {
            listaCategorias.add(
                categoriaRepository.findByNome(nomeCategoria).orElse(new Categoria(nomeCategoria)));
          }
        });

    // 1
    return new Sala(rodadas, maximoJogadores, senha, listaCategorias, letras, tempoJogo);
  }
}
