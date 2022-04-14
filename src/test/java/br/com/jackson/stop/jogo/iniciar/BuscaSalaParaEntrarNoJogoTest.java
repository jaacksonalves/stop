package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.sala.SalaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static br.com.jackson.stop.sala.SalaFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class BuscaSalaParaEntrarNoJogoTest {

  public static final long SALA_ID = 1L;
  @Mock private SalaRepository salaRepository;
  @InjectMocks private BuscaSalaParaIniciarJogo service;

  @Nested
  class BuscaSalaAleatoriaTest {

    @Test
    @DisplayName("Deve retornar uma sala aleatória, livre sem senha")
    void teste1() {
      var sala = criaSalaSemSenha();
      when(salaRepository.findAll()).thenReturn(List.of(sala));

      var salaRetornada = service.buscaSalaAleatoria(Assertions::fail).orElseGet(Assertions::fail);

      assertAll(() -> assertEquals(sala.getId(), salaRetornada.getId()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando só há salas com senha")
    void teste2() {
      var sala = criaSalaComSenha();
      when(salaRepository.findAll()).thenReturn(List.of(sala));

      var ex =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  service.buscaSalaAleatoria(
                      lancaResponseStatusException(BAD_REQUEST, "Deve lançar exceção")));

      assertEquals("Deve lançar exceção", ex.getReason());
      assertEquals(BAD_REQUEST, ex.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há salas com vaga")
    void teste3() {
      var sala = criaSalaSemSenha();
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());
      sala.adicionarUsuario(criaUsuario());
      when(salaRepository.findAll()).thenReturn(List.of(sala));

      var ex =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  service.buscaSalaAleatoria(
                      lancaResponseStatusException(NOT_FOUND, "Deve lançar exceção")));

      assertEquals("Deve lançar exceção", ex.getReason());
      assertEquals(NOT_FOUND, ex.getStatus());
    }

    @Test
    @DisplayName("Deve retornar optional vazio quando não há salas e nenhuma exceção foi lançada")
    void teste4() {
      var request = new IniciarJogoRequest("nome");
      when(salaRepository.findAll()).thenReturn(List.of());

      var possivelSala = service.buscaSalaAleatoria(() -> request.setSenha("123"));

      assertAll(
          () -> assertTrue(possivelSala.isEmpty()), () -> assertEquals("123", request.getSenha()));
    }
  }

  @Nested
  class BuscaSalaEspecificaTest {

    @Test
    @DisplayName("Deve retornar uma sala específica, livre sem senha")
    void teste1() {
      var sala = criaSalaSemSenha();
      var request = new IniciarJogoRequest("nome");
      when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));

      var salaRetornada =
          service.buscaSalaEspecifica(
              sala.getId(), request, Assertions::fail, Assertions::fail, Assertions::fail);

      assertTrue(salaRetornada.isPresent());
      assertEquals(sala.getId(), salaRetornada.get().getId());
    }

    @Test
    @DisplayName(
        "Deve retornar uma sala específica, privada com senha, quando a senha está correta")
    void teste2() {
      var sala = criaSalaComSenha();
      var request = new IniciarJogoRequest("nome");
      request.setSenha(sala.getSenha());
      when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));

      var salaRetornada =
          service.buscaSalaEspecifica(
              sala.getId(), request, Assertions::fail, Assertions::fail, Assertions::fail);

      assertTrue(salaRetornada.isPresent());
      assertEquals(sala.getId(), salaRetornada.get().getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há salas")
    void teste3() {
      var request = new IniciarJogoRequest("nome");
      when(salaRepository.findById(SALA_ID)).thenReturn(Optional.empty());

      var ex =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  service.buscaSalaEspecifica(
                      SALA_ID,
                      request,
                      lancaResponseStatusException(NOT_FOUND, "Sala não encontrada"),
                      Assertions::fail,
                      Assertions::fail));

      assertAll(
          () -> assertEquals(NOT_FOUND, ex.getStatus()),
          () -> assertEquals("Sala não encontrada", ex.getReason()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha informada está incorreta")
    void teste4() {
      var sala = criaSalaComSenha();
      var id = sala.getId();
      var request = new IniciarJogoRequest("nome");
      request.setSenha("senhaIncorreta");
      when(salaRepository.findById(id)).thenReturn(Optional.of(sala));

      var ex =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  service.buscaSalaEspecifica(
                      id,
                      request,
                      Assertions::fail,
                      Assertions::fail,
                      lancaResponseStatusException(BAD_REQUEST, "Senha inválida")));

      assertAll(
          () -> assertEquals(BAD_REQUEST, ex.getStatus()),
          () -> assertEquals("Senha inválida", ex.getReason()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a sala não tem vaga disponível")
    void teste5() {
      var sala = criaSalaSemSenhaSemVaga();
      var id = sala.getId();
      var request = new IniciarJogoRequest("nome");

      when(salaRepository.findById(id)).thenReturn(Optional.of(sala));

      var ex =
          assertThrows(
              ResponseStatusException.class,
              () ->
                  service.buscaSalaEspecifica(
                      id,
                      request,
                      Assertions::fail,
                      lancaResponseStatusException(BAD_REQUEST, "Sala não tem vaga"),
                      Assertions::fail));

      assertAll(
          () -> assertEquals(BAD_REQUEST, ex.getStatus()),
          () -> assertEquals("Sala não tem vaga", ex.getReason()));
    }

    @Test
    @DisplayName("Deve retornar optional vazio quando não há salas e nenhuma exceção foi lançada")
    void teste6() {
      var request = new IniciarJogoRequest("nome");
      when(salaRepository.findById(SALA_ID)).thenReturn(Optional.empty());

      var possivelSala =
          service.buscaSalaEspecifica(
              SALA_ID, request, () -> request.setSenha("123"), Assertions::fail, Assertions::fail);

      assertAll(
          () -> assertTrue(possivelSala.isEmpty()), () -> assertEquals("123", request.getSenha()));
    }
  }

  private Runnable lancaResponseStatusException(HttpStatus status, String mensagem) {
    return () -> {
      throw new ResponseStatusException(status, mensagem);
    };
  }
}
