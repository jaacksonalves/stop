package br.com.jackson.stop.sala;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.jackson.stop.sala.SalaFactory.criaSalaSemSenha;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalaTest {

  @Nested
  class MetodoAdicionaSenhaTest {

    @Test
    void teste1() {
      var sala = criaSalaSemSenha();
      sala.adicionaSenha("123");

      assertEquals("123", sala.getSenha());
      assertTrue(sala.isPrivada());
    }
  }
}
