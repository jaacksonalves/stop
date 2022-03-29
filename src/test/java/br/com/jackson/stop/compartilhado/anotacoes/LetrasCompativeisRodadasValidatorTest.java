package br.com.jackson.stop.compartilhado.anotacoes;

import br.com.jackson.stop.sala.NovaSalaRequest;
import br.com.jackson.stop.sala.TempoJogo;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LetrasCompativeisRodadasValidatorTest {

  LetrasCompativeisRodadasValidator validator = new LetrasCompativeisRodadasValidator();

  @Test
  @DisplayName("Deve validar quando a quantidade de letras for igual ao numero de rodadas")
  void teste1() {
    var request = criaNovaSalaRequest(2, List.of("A", "B"));

    assertTrue(validator.isValid(request, null));
  }

  @Property(tries = 12)
  @Label("Deve validar quando a quantidade de rodadas for menor que a quantidade de letras")
  void teste2(@ForAll @IntRange(min = 1, max = 11) int rodadas) {
    var request =
        criaNovaSalaRequest(
            rodadas, List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"));

    assertTrue(validator.isValid(request, null));
  }

  @Property(tries = 100)
  @Label("Não deve validar quando a quantidade de rodadas for maior que a quantidade de letras")
  void teste3(@ForAll @IntRange(min = 3) int rodadas) {
    var request = criaNovaSalaRequest(rodadas, List.of("A", "B"));

    assertFalse(validator.isValid(request, null));
  }

  private NovaSalaRequest criaNovaSalaRequest(int rodadas, List<String> letras) {
    return new NovaSalaRequest(
        rodadas, 10, "senha", List.of("Categoria 1", "Categoria 2"), letras, TempoJogo.MEDIO);
  }
}
