package br.com.jackson.stop.compartilhado.anotacoes;

import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;

import static br.com.jackson.stop.sala.SalaFactory.criaNovaSalaRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LetrasCompativeisRodadasTest {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  @DisplayName("Deve validar quando a quantidade de letras for igual ao numero de rodadas")
  void teste1() {
    var request = criaNovaSalaRequest("senha", 4, List.of("A", "B", "C", "D"));

    assertEquals(0, validator.validate(request).size());
  }

  @Property(tries = 8)
  @Label("Deve validar quando a quantidade de rodadas for menor que a quantidade de letras")
  void teste2(@ForAll @IntRange(min = 4, max = 11) int rodadas) {
    var request =
        criaNovaSalaRequest(
            "senha", rodadas, List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"));

    assertEquals(0, validator.validate(request).size());
  }

  @Property(tries = 8)
  @Label("Não deve validar quando a quantidade de rodadas for maior que a quantidade de letras")
  void teste3(@ForAll @IntRange(min = 4, max = 12) int rodadas) {
    var request = criaNovaSalaRequest("senha", rodadas, List.of("A"));

    var validacao = validator.validate(request);

    assertEquals(1, validacao.size());
  }
}
