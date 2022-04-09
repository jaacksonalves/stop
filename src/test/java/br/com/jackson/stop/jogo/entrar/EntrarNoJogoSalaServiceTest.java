package br.com.jackson.stop.jogo.entrar;

import br.com.jackson.stop.sala.SalaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static br.com.jackson.stop.sala.SalaFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class EntrarNoJogoSalaServiceTest {

  @Mock private SalaRepository salaRepository;
  @InjectMocks private EntrarNoJogoSalaService service;

  @Nested
  class BuscaSalaAleatoriaTest {

    @Test
    @DisplayName("Deve retornar uma sala aleatória, livre sem senha")
    void teste1() {
      var sala = criaSalaSemSenha();
      when(salaRepository.findAll()).thenReturn(List.of(sala));

      var salaRetornada = service.buscaSalaAleatoria();

      assertAll(() -> assertEquals(sala.getId(), salaRetornada.getId()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há salas")
    void teste2() {
      when(salaRepository.findAll()).thenReturn(List.of());

      var ex = assertThrows(ResponseStatusException.class, () -> service.buscaSalaAleatoria());

      assertAll(
          () -> assertEquals(NOT_FOUND, ex.getStatus()),
          () -> assertEquals("Não há salas disponíveis", ex.getReason()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando só há salas com senha")
    void teste3() {
      var sala = criaSalaComSenha();
      when(salaRepository.findAll()).thenReturn(List.of(sala));

      var ex = assertThrows(ResponseStatusException.class, () -> service.buscaSalaAleatoria());

      assertAll(
          () -> assertEquals(NOT_FOUND, ex.getStatus()),
          () -> assertEquals("Não há salas disponíveis", ex.getReason()));
    }
  }

  @Nested
  class BuscaSalaEspecificaTest {

    @Test
    @DisplayName("Deve retornar uma sala específica, livre sem senha")
    void teste1() {
      var sala = criaSalaSemSenha();
      var request = new EntrarNoJogoRequest("nome");
      when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));

      var salaRetornada = service.buscaSalaEspecifica(sala.getId(), request);

      assertAll(() -> assertEquals(sala.getId(), salaRetornada.getId()));
    }

    @Test
    @DisplayName(
        "Deve retornar uma sala específica, privada com senha, quando a senha está correta")
    void teste2() {
      var sala = criaSalaComSenha();
      var request = new EntrarNoJogoRequest("nome");
      request.setSenha(sala.getSenha());
      when(salaRepository.findById(sala.getId())).thenReturn(Optional.of(sala));

      var salaRetornada = service.buscaSalaEspecifica(sala.getId(), request);

      assertAll(() -> assertEquals(sala.getId(), salaRetornada.getId()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há salas")
    void teste3() {
      var request = new EntrarNoJogoRequest("nome");
      when(salaRepository.findById(1L)).thenReturn(Optional.empty());

      var ex =
          assertThrows(
              ResponseStatusException.class, () -> service.buscaSalaEspecifica(1L, request));

      assertAll(
          () -> assertEquals(NOT_FOUND, ex.getStatus()),
          () -> assertEquals("Sala não encontrada", ex.getReason()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a senha informada está incorreta")
    void teste4() {
      var sala = criaSalaComSenha();
      var id = sala.getId();
      var request = new EntrarNoJogoRequest("nome");
      request.setSenha("senhaIncorreta");
      when(salaRepository.findById(id)).thenReturn(Optional.of(sala));

      var ex =
          assertThrows(
              ResponseStatusException.class, () -> service.buscaSalaEspecifica(id, request));

      assertAll(
          () -> assertEquals(BAD_REQUEST, ex.getStatus()),
          () -> assertEquals("Senha inválida", ex.getReason()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando a sala não tem vaga disponível")
    void teste5() {
      var sala = criaSalaSemSenhaSemVaga();
      var id = sala.getId();
      var request = new EntrarNoJogoRequest("nome");

      when(salaRepository.findById(id)).thenReturn(Optional.of(sala));

      var ex =
              assertThrows(
                      ResponseStatusException.class, () -> service.buscaSalaEspecifica(id, request));

      assertAll(
              () -> assertEquals(BAD_REQUEST, ex.getStatus()),
              () -> assertEquals("Sala não tem vaga", ex.getReason()));
    }
  }
}