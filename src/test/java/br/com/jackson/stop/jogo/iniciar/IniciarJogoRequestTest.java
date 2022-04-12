package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IniciarJogoRequestTest {

  private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);

  @Nested
  class ToUsuarioTest {

    @Test
    @DisplayName("Deve criar um novo Usuario quando id não for informado")
    void teste1() {
      var request = new IniciarJogoRequest("nome");
      var possivelUsuario = request.toUsuario(usuarioRepository);
      var usuario = possivelUsuario.orElseGet(Assertions::fail);

      verifyNoMoreInteractions(usuarioRepository);

      assertAll(() -> assertEquals("nome", usuario.getNome()));
    }

    @Test
    @DisplayName("Deve retornar um Usuario quando id for informado e Usuario existir")
    void teste2() {
      var request = new IniciarJogoRequest("nome");
      request.setIdUsuario(1L);
      var novoUsuario = new Usuario(request.getNomeUsuario());

      when(usuarioRepository.findById(request.getIdUsuario())).thenReturn(Optional.of(novoUsuario));

      var possivelUsuario = request.toUsuario(usuarioRepository);
      var usuario = possivelUsuario.orElseGet(Assertions::fail);

      verify(usuarioRepository).findById(request.getIdUsuario());
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(() -> assertEquals(request.getNomeUsuario(), usuario.getNome()));
    }

    @Test
    @DisplayName("Deve retornar um Optional vazio quando id for informado e Usuario não existir")
    void teste3() {
      var request = new IniciarJogoRequest("nome");
      request.setIdUsuario(1L);

      when(usuarioRepository.findById(request.getIdUsuario())).thenReturn(Optional.empty());

      var possivelUsuario = request.toUsuario(usuarioRepository);

      verify(usuarioRepository).findById(request.getIdUsuario());
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(() -> assertTrue(possivelUsuario.isEmpty()));
    }
  }
}
