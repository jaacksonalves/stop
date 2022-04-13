package br.com.jackson.stop.jogo.iniciar;

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
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class BuscaUsuarioParaIniciarJogoTest {

  public static final long ID_USUARIO = 1L;
  @Mock private UsuarioRepository usuarioRepository;
  @InjectMocks private BuscaUsuarioParaIniciarJogo service;

  @Nested
  class GetUsuarioTest {

    @Test
    @DisplayName("Deve retornar o usuário quando o usuário não estiver num jogo")
    void teste1() {
      var request = new IniciarJogoRequest("nome");
      var usuario = new Usuario("nome");
      request.setIdUsuario(ID_USUARIO);

      when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(usuario));

      var usuarioRetornado = service.getUsuario(request);
      verify(usuarioRepository).findById(ID_USUARIO);
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(() -> assertEquals(usuario.getNome(), usuarioRetornado.getNome()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário já estiver num jogo")
    void teste2() {
      var request = new IniciarJogoRequest("nome");
      var usuario = new Usuario("nome");
      var sala = SalaFactory.criaSalaSemSenha();
      sala.adicionarUsuario(usuario);
      setField(usuario, "sala", sala);
      request.setIdUsuario(ID_USUARIO);

      when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.of(usuario));

      var ex = assertThrows(ResponseStatusException.class, () -> service.getUsuario(request));
      verify(usuarioRepository).findById(ID_USUARIO);
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(
          () -> assertEquals(BAD_REQUEST, ex.getStatus()),
          () -> assertEquals("Usuário já está jogando em outra sala", ex.getReason()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não existir pelo id informado")
    void teste3() {
      var request = new IniciarJogoRequest("nome");
      request.setIdUsuario(ID_USUARIO);

      when(usuarioRepository.findById(ID_USUARIO)).thenReturn(Optional.empty());

      var ex = assertThrows(ResponseStatusException.class, () -> service.getUsuario(request));
      verify(usuarioRepository).findById(ID_USUARIO);
      verifyNoMoreInteractions(usuarioRepository);

      assertAll(
          () -> assertEquals(NOT_FOUND, ex.getStatus()),
          () -> assertEquals("Usuário não encontrado", ex.getReason()));
    }
  }
}
