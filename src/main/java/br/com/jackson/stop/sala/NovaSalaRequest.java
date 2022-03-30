package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.compartilhado.anotacoes.LetrasCompativeisRodadas;
import br.com.jackson.stop.compartilhado.anotacoes.LetrasPermitidas;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@ICP(9.5)
@LetrasCompativeisRodadas
public record NovaSalaRequest(//3.5
        @NotNull @Range(min = 1, max = 12) int rodadas,
        @NotNull @Range(min = 1, max = 12) int maximoJogadores,
        String senha,
        @NotNull @NotEmpty @UniqueElements List<String> categorias,
        @NotNull @NotEmpty @UniqueElements @LetrasPermitidas List< String> letras,
        @NotNull TempoJogo tempoJogo) {

  // 1
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
                  return;
              }
              listaCategorias.add(
                        categoriaRepository.findByNome(nomeCategoria).orElse(new Categoria(nomeCategoria)));

            });

    //1
    var novaSala = new Sala(rodadas, maximoJogadores, listaCategorias, letras, tempoJogo);
    //1
    if (StringUtils.hasText(senha)) {
        novaSala.adicionaSenha(this.senha);
    }
    return novaSala;
  }

    public boolean letrasCompativeisComRodadas() {
      //1
      return this.letras.size() >= this.rodadas;
    }
}
