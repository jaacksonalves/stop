package br.com.jackson.stop.jogo.entrar;

import br.com.jackson.stop.sala.SalaFactory;
import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class EntrarNoJogoUsuarioServiceTest {

  @Mock private UsuarioRepository usuarioRepository;
  @InjectMocks private EntrarNoJogoUsuarioService service;

  @Nested
  class GetUsuarioTest {

    @Test
    @DisplayName("Deve retornar o usuário quando o usuário não estiver num jogo")
    void teste1() {
      var request = new EntrarNoJogoRequest("nome");
      var usuario = new Usuario("nome");
      request.setIdUsuario(1L);
      when(usuarioRepository.findById(request.getIdUsuario())).thenReturn(Optional.of(usuario));

      var usuarioRetornado = service.getUsuario(request);
      verify(usuarioRepository).findById(request.getIdUsuario());
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(() -> assertEquals(usuario.getNome(), usuarioRetornado.getNome()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário já estiver num jogo")
    void teste2() {
      var request = new EntrarNoJogoRequest("nome");
      var usuario = new Usuario("nome");
      var sala = SalaFactory.criaSalaSemSenha();
      sala.adicionarUsuario(usuario);
      setField(usuario, "sala", sala);
      request.setIdUsuario(1L);

      when(usuarioRepository.findById(request.getIdUsuario())).thenReturn(Optional.of(usuario));

      var ex = assertThrows(ResponseStatusException.class, () -> service.getUsuario(request));
      verify(usuarioRepository).findById(request.getIdUsuario());
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(
          () -> assertEquals(BAD_REQUEST, ex.getStatus()),
          () -> assertEquals("Usuário já está jogando em outra sala", ex.getReason()));
    }
  }
}
