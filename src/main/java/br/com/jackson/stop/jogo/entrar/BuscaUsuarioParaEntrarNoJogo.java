package br.com.jackson.stop.jogo.entrar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Service
@ICP(4)
public class BuscaUsuarioParaEntrarNoJogo {

  // 1
  private final UsuarioRepository usuarioRepository;

  @Autowired
  public BuscaUsuarioParaEntrarNoJogo(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  // 1
  public Usuario getUsuario(EntrarNoJogoRequest request) {
    // 1
    var usuario =
        request
            .toUsuario(usuarioRepository)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

    this.validaSeUsuarioPodeJogar(usuario);
    return usuario;
  }

  private void validaSeUsuarioPodeJogar(Usuario usuario) {
    // 1
    if (!usuario.podeJogar()) {
      throw new ResponseStatusException(BAD_REQUEST, "Usuário já está jogando em outra sala");
    }
  }
}
