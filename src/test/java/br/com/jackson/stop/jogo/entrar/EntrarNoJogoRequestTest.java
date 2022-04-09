package br.com.jackson.stop.jogo.entrar;

import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class EntrarNoJogoRequestTest {

  private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);

  @Nested
  class ToUsuarioTest {

    @Test
    @DisplayName("Deve criar um novo Usuario quando id não for informado")
    void teste1() {
      var request = new EntrarNoJogoRequest("nome");
      var usuario = request.toUsuario(usuarioRepository);

      verifyNoMoreInteractions(usuarioRepository);

      assertAll(() -> assertEquals("nome", usuario.getNome()));
    }

    @Test
    @DisplayName("Deve retornar um Usuario quando id for informado e Usuario existir")
    void teste2() {
      var request = new EntrarNoJogoRequest("nome");
      request.setIdUsuario(1L);
      var usuario = new Usuario(request.getNomeUsuario());

      when(usuarioRepository.findById(request.getIdUsuario())).thenReturn(Optional.of(usuario));

      var usuarioEncontrado = request.toUsuario(usuarioRepository);

      verify(usuarioRepository).findById(request.getIdUsuario());
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(() -> assertEquals(request.getNomeUsuario(), usuarioEncontrado.getNome()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando id for informado e Usuario não existir")
    void teste3() {
      var request = new EntrarNoJogoRequest("nome");
      request.setIdUsuario(1L);

      when(usuarioRepository.findById(request.getIdUsuario())).thenReturn(Optional.empty());

      var ex =
          assertThrows(ResponseStatusException.class, () -> request.toUsuario(usuarioRepository));

      verify(usuarioRepository).findById(request.getIdUsuario());
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(
          () -> assertEquals(NOT_FOUND, ex.getStatus()),
          () -> assertEquals("Usuário não encontrado", ex.getReason()));
    }
  }
}
