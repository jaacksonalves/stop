package br.com.jackson.stop.sala;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static br.com.jackson.stop.sala.SalaFactory.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SalaPartial1Test {

  @Nested
  class DisponivelParaJogoAleatorioTest {

    @Test
    @DisplayName("Deve estar disponível quando a sala não é privada e tem vaga")
    void teste1() {
      var sala = criaSalaSemSenha();
      var salaPartial1 = new SalaPartial1(sala);
      assertTrue(salaPartial1.disponivelParaJogoAleatorio());
    }

    @Test
    @DisplayName("Não deve estar disponível quando a sala é privada e tem vaga")
    void teste2() {
      var sala = criaSalaComSenha();
      var salaPartial1 = new SalaPartial1(sala);
      assertFalse(salaPartial1.disponivelParaJogoAleatorio());
    }

    @Test
    @DisplayName("Não deve estar disponível quando a sala não é privada e não tem vaga")
    void teste3() {
      var sala = criaSalaSemSenha();
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());

      var salaPartial1 = new SalaPartial1(sala);
      assertFalse(salaPartial1.disponivelParaJogoAleatorio());
    }
  }

  @Nested
  class ValidaEntradaTest {

    @Test
    @DisplayName("Deve validar quando sala não é privada")
    void teste1() {
      var sala = criaSalaSemSenha();
      var salaPartial1 = new SalaPartial1(sala);
      assertTrue(salaPartial1.validaEntrada(null));
    }

    @Test
    @DisplayName("Deve validar quando sala é privada e senha está correta")
    void teste2() {
      var sala = criaSalaComSenha();
      var salaPartial1 = new SalaPartial1(sala);
      assertTrue(salaPartial1.validaEntrada(sala.getSenha()));
    }

    @Test
    @DisplayName("Deve validar quando sala é privada e senha está incorreta")
    void teste3() {
      var sala = criaSalaComSenha();
      var salaPartial1 = new SalaPartial1(sala);
      assertFalse(salaPartial1.validaEntrada("senha incorreta"));
    }
  }

  @Nested
  class TemVagaTest {

    @Test
    @DisplayName("Deve ter vaga quando não tem jogadores na sala")
    void teste1() {
      var sala = criaSalaSemSenha();
      var salaPartial1 = new SalaPartial1(sala);
      assertTrue(salaPartial1.temVaga());
    }

    @Test
    @DisplayName("Deve ter vaga quando tem menos jogadores atuais do que o máximo permitido")
    void teste2() {
      var sala = criaSalaSemSenha();
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());

      var salaPartial1 = new SalaPartial1(sala);
      assertTrue(salaPartial1.temVaga());
    }

    @Test
    @DisplayName("Não deve ter vaga quando sala está lotada")
    void teste3() {
      var sala = criaSalaSemSenha();
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());

      var salaPartial1 = new SalaPartial1(sala);
      assertFalse(salaPartial1.temVaga());
    }
  }
}
