package br.com.jackson.stop.sala;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.jackson.stop.sala.SalaFactory.criaSalaComSenha;
import static br.com.jackson.stop.sala.SalaFactory.criaSalaSemSenha;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class SalaTest {

  @Nested
  class MetodoAdicionaSenhaTest {

    @Test
    @DisplayName("Deve adicionar senha a sala")
    void teste1() {
      var sala = criaSalaSemSenha();
      sala.adicionaSenha("123");

      assertEquals("123", sala.getSenha());
      assertTrue(sala.isPrivada());
    }
  }

  @Nested
  class MetodoSalaDisponivelTest {

    @Test
    @DisplayName("Deve retornar true se a sala sem senha estiver disponivel")
    void teste1() {
      var sala = criaSalaSemSenha();

      assertTrue(sala.salaDisponivel());
    }

    @Test
    @DisplayName("Deve retornar true se a sala com senha estiver disponivel")
    void teste2() {
      var sala = criaSalaComSenha();

      assertTrue(sala.salaDisponivel());
    }

    @Test
    @DisplayName("Deve retornar false se a sala com senha estiver ocupada")
    void teste3() {
      var sala = criaSalaComSenha();
      setField(sala, "jogadoresAtuais", sala.getMaximoJogadores());

      assertFalse(sala.salaDisponivel());
    }

    @Test
    @DisplayName("Deve retornar false se a sala sem senha estiver ocupada")
    void teste4() {
      var sala = criaSalaSemSenha();
      setField(sala, "jogadoresAtuais", sala.getMaximoJogadores());

      assertFalse(sala.salaDisponivel());
    }
  }
}
