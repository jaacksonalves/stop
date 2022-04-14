package br.com.jackson.stop.usuario;

import br.com.jackson.stop.sala.SalaFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UsuarioTest {

  @Nested
  class PodeJogarTest {

    @Test
    @DisplayName("Pode jogar quando não está jogando atualmente")
    void teste1() {
      Usuario usuario = new Usuario("nome");
      assertTrue(usuario.podeJogar());
    }

    @Test
    @DisplayName("Não pode jogar quando está jogando atualmente")
    void teste2() {
      Usuario usuario = new Usuario("nome");
      usuario.setSala(SalaFactory.criaSalaSemSenha());
      assertFalse(usuario.podeJogar());
    }
  }
}
