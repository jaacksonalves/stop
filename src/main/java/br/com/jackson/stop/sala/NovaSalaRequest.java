package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.compartilhado.anotacoes.LetrasCompativeisRodadas;
import br.com.jackson.stop.compartilhado.anotacoes.LetrasPermitidas;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ICP(8.5)
@LetrasCompativeisRodadas
public record NovaSalaRequest(//3.5
        @NotNull @Range(min = 4, max = 12) int rodadas,
        @NotNull @Range(min = 4, max = 12) int maximoJogadores,
        String senha,
        @NotNull @NotEmpty @UniqueElements List<String> categorias,
        @NotNull @NotEmpty @UniqueElements @LetrasPermitidas List< String> letras,
        @NotNull TempoJogo tempoJogo) {

  // 1
  public Sala toSala(CategoriaRepository categoriaRepository) {

     var listaCategorias =
            this.categorias.stream()
                .map(
                    nomeCategoria ->
                        categoriaRepository
                            .findByNome(nomeCategoria)
                                //1
                            .orElseGet(()->new Categoria(nomeCategoria)))
                .toList();

    //1
    var novaSala = new Sala(rodadas, maximoJogadores, listaCategorias, letras, tempoJogo);
    //1
    if (StringUtils.hasText(this.senha)) {
        novaSala.adicionaSenha(this.senha);
    }
    return novaSala;
  }

    public boolean letrasCompativeisComRodadas() {
      //1
      return this.letras.size() >= this.rodadas;
    }
}
