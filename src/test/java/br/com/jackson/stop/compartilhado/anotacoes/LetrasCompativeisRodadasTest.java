package br.com.jackson.stop.compartilhado.anotacoes;

import br.com.jackson.stop.sala.NovaSalaRequest;
import br.com.jackson.stop.sala.TempoJogo;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LetrasCompativeisRodadasTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Deve validar quando a quantidade de letras for igual ao numero de rodadas")
  void teste1() {
    var request = criaNovaSalaRequest(2, List.of("A", "B"));

    assertEquals(0, validator.validate(request).size());
  }

  @Property(tries = 12)
  @Label("Deve validar quando a quantidade de rodadas for menor que a quantidade de letras")
  void teste2(@ForAll @IntRange(min = 1, max = 11) int rodadas) {
    var request =
        criaNovaSalaRequest(
            rodadas, List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"));

    assertEquals(0, validator.validate(request).size());
  }

  @Property(tries = 11)
  @Label("Não deve validar quando a quantidade de rodadas for maior que a quantidade de letras")
  void teste3(@ForAll @IntRange(min = 2, max = 12) int rodadas) {
    var request = criaNovaSalaRequest(rodadas, List.of("A"));

    var validacao = validator.validate(request);

    assertEquals(1, validacao.size());
    assertEquals(
        "A quantidade de letras deve ser maior ou igual ao número de rodadas",
        validacao.iterator().next().getMessage());
  }

  private NovaSalaRequest criaNovaSalaRequest(int rodadas, List<String> letras) {
    return new NovaSalaRequest(
        rodadas, 10, "senha", List.of("Categoria 1", "Categoria 2"), letras, TempoJogo.MEDIO);
  }
}
