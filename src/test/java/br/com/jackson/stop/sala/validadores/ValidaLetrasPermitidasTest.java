package br.com.jackson.stop.sala.validadores;

import br.com.jackson.stop.sala.NovaSalaRequest;
import br.com.jackson.stop.sala.TempoJogo;
import net.jqwik.api.ForAll;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.BeforeTry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidaLetrasPermitidasTest {

  private BeanPropertyBindingResult erros;

  @BeforeTry
  void preparacao() {
    erros = new BeanPropertyBindingResult(new Object(), "teste");
  }

  @Property(tries = 26)
  @Label("Deve permitir letras maiúsculas de A a Z")
  void teste1(
      @ForAll @Size(1) List<@StringLength(1) @UpperChars @UniqueElements String> listaLetras) {
    var request =
        new NovaSalaRequest(1, 1, null, List.of("Categoria"), listaLetras, TempoJogo.MEDIO);
    new ValidaLetrasPermitidas().validate(request, erros);

    assertTrue(erros.getAllErrors().isEmpty());
  }

  @Property(tries = 26)
  @Label("Não deve permitir letras minúsculas de a a z")
  void teste2(
      @ForAll @Size(1) List<@StringLength(1) @LowerChars @UniqueElements String> listaLetras) {
    var request =
        new NovaSalaRequest(1, 1, null, List.of("Categoria"), listaLetras, TempoJogo.MEDIO);
    new ValidaLetrasPermitidas().validate(request, erros);

    assertFalse(erros.getAllErrors().isEmpty());
    assertEquals(
        "No campo Letras, apenas letras maiúsculas de A a Z são permitidas",
        erros.getAllErrors().get(0).getCode());
  }

  @Property(tries = 100)
  @Label("Não deve permitir números")
  void teste3(
      @ForAll @Size(1) List<@StringLength(2) @NumericChars @UniqueElements String> listaLetras) {
    var request =
        new NovaSalaRequest(1, 1, null, List.of("Categoria"), listaLetras, TempoJogo.MEDIO);
    new ValidaLetrasPermitidas().validate(request, erros);

    assertFalse(erros.getAllErrors().isEmpty());
    assertEquals(
        "No campo Letras, apenas letras maiúsculas de A a Z são permitidas",
        erros.getAllErrors().get(0).getCode());
  }

  // Mesmo lendo a doc do Jqwik, não consegui gerar símbolos especiais
  @Test
  @DisplayName("Não deve permitir símbolos")
  void teste4() {
    erros = new BeanPropertyBindingResult(new Object(), "teste");
    var listaLetras = List.of("@", "&", "*", "!", "?", ".", ",", ";", ":", "|", "/", "Ç", "ã");
    var request =
        new NovaSalaRequest(1, 1, null, List.of("Categoria"), listaLetras, TempoJogo.MEDIO);
    new ValidaLetrasPermitidas().validate(request, erros);

    assertFalse(erros.getAllErrors().isEmpty());
    assertEquals(
        "No campo Letras, apenas letras maiúsculas de A a Z são permitidas",
        erros.getAllErrors().get(0).getCode());
  }
}
