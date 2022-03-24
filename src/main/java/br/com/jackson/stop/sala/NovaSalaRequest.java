package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@ICP(8.5)
public record NovaSalaRequest(@NotNull @Positive @Max(12) int rodadas,
                              @NotNull @Positive @Max(12) int maximoJogadores,
                              String senha,
                              @NotNull @NotEmpty List<String> categorias,
                              @NotNull @NotEmpty List<String> letras,
                              @NotNull TempoJogo tempoJogo) {

  // 2
  public Sala toSala(CategoriaRepository categoriaRepository) {
      //1
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

    return new Sala(rodadas, maximoJogadores, senha, listaCategorias, letras, tempoJogo);
  }



}
