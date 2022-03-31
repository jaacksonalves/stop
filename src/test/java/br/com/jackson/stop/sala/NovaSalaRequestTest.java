package br.com.jackson.stop.sala;

import net.jqwik.api.ForAll;
import net.jqwik.api.Group;
import net.jqwik.api.Label;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static br.com.jackson.stop.sala.SalaFactory.CATEGORIA_1;
import static br.com.jackson.stop.sala.SalaFactory.criaNovaSalaRequest;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NovaSalaRequestTest {

  @Nested
  class MetodoLetrasCompativeisComRodadasJUnitTest {

    @Test
    @DisplayName("Deve validar quando a quantidade de letras é igual com as rodadas")
    void teste1() {
      var request = criaNovaSalaRequest("senha",2, List.of("A", "B"));

      assertTrue(request.letrasCompativeisComRodadas());
    }

    @Test
    @DisplayName("Deve validar quando a quantidade de letras é uma maior que as rodadas")
    void teste2() {
      var request = criaNovaSalaRequest("senha",2, List.of("A", "B", "C"));

      assertTrue(request.letrasCompativeisComRodadas());
    }

    @Test
    @DisplayName("Não deve validar quando a quantidade de letras é uma menor que as rodadas")
    void teste3() {
      var request = criaNovaSalaRequest("senha",2, List.of("A"));

      assertFalse(request.letrasCompativeisComRodadas());
    }
  }

  @Group
  class MetodoLetrasCompativeisComRodadasJqwikTest {

    @Property
    @Label("Deve validar quando a quantidade de letras é maior que as rodadas")
    void teste1(
        @ForAll @IntRange(min = 1, max = 11) int rodadas,
        @ForAll @Size(12) @UniqueElements List<@UpperChars @StringLength(1) String> letras) {
      var request = criaNovaSalaRequest("senha",rodadas, letras);

      assertTrue(request.letrasCompativeisComRodadas());
    }

    @Property
    @Label("Não deve validar quando a quantidade de letras é menor que as rodadas")
    void teste2(
        @ForAll @IntRange(min = 2, max = 12) int rodadas,
        @ForAll @Size(1) @UniqueElements List<@UpperChars @StringLength(1) String> letras) {
      var request = criaNovaSalaRequest("senha",rodadas, letras);

      assertFalse(request.letrasCompativeisComRodadas());
    }
  }

  @Nested
  class MetodoToSalaJUnitTest {
    CategoriaRepository categoriaRepositoryMock = mock(CategoriaRepository.class);

    @Test
    @DisplayName("Deve retornar uma sala completa com senha, privada e categoria não existente")
    void teste1() {
      var categoria = new Categoria(CATEGORIA_1);
      when(categoriaRepositoryMock.existsByNome(CATEGORIA_1)).thenReturn(false);
      when(categoriaRepositoryMock.save(categoria)).thenReturn(categoria);

      var request = criaNovaSalaRequest("senha", 2, List.of("A", "B"));

      var sala = request.toSala(categoriaRepositoryMock);
      Assertions.assertAll(
          () -> assertNotNull(sala),
          () -> assertEquals(request.rodadas(), sala.getRodadas()),
          () -> assertEquals(request.letras(), sala.getLetras()),
          () -> assertEquals(request.maximoJogadores(), sala.getMaximoJogadores()),
          () -> assertEquals(request.tempoJogo(), sala.getTempoJogo()),
          () -> assertEquals(request.senha(), sala.getSenha()),
          () -> assertTrue(sala.isPrivada()));
    }

    @Test
    @DisplayName(
        "Deve retornar uma sala completa sem senha, livre (não privada) e categoria não existente")
    void teste2() {
      var categoria = new Categoria(CATEGORIA_1);
      when(categoriaRepositoryMock.existsByNome(CATEGORIA_1)).thenReturn(false);
      when(categoriaRepositoryMock.save(categoria)).thenReturn(categoria);

      var request = criaNovaSalaRequest(null, 2, List.of("A", "B"));

      var sala = request.toSala(categoriaRepositoryMock);
      Assertions.assertAll(
          () -> assertNotNull(sala),
          () -> assertEquals(request.rodadas(), sala.getRodadas()),
          () -> assertEquals(request.letras(), sala.getLetras()),
          () -> assertEquals(request.maximoJogadores(), sala.getMaximoJogadores()),
          () -> assertEquals(request.tempoJogo(), sala.getTempoJogo()),
          () -> assertEquals(request.senha(), sala.getSenha()),
          () -> assertFalse(sala.isPrivada()));
    }

    @Test
    @DisplayName("Deve retornar uma sala completa com senha, privada e categoria existente")
    void teste3() {
      var categoria = new Categoria(CATEGORIA_1);
      when(categoriaRepositoryMock.existsByNome(CATEGORIA_1)).thenReturn(true);
      when(categoriaRepositoryMock.findByNome(CATEGORIA_1)).thenReturn(Optional.of(categoria));

      var request = criaNovaSalaRequest("senha", 2, List.of("A", "B"));

      var sala = request.toSala(categoriaRepositoryMock);
      Assertions.assertAll(
          () -> assertNotNull(sala),
          () -> assertEquals(request.rodadas(), sala.getRodadas()),
          () -> assertEquals(request.letras(), sala.getLetras()),
          () -> assertEquals(request.maximoJogadores(), sala.getMaximoJogadores()),
          () -> assertEquals(request.tempoJogo(), sala.getTempoJogo()),
          () -> assertEquals(request.senha(), sala.getSenha()),
          () -> assertTrue(sala.isPrivada()));
    }
  }
}
