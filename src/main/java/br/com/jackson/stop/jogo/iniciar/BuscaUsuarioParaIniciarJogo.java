package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@ICP(5)
public class BuscaUsuarioParaIniciarJogo {

  // 1
  private final UsuarioRepository usuarioRepository;

  @Autowired
  public BuscaUsuarioParaIniciarJogo(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  // 1
  public Optional<Usuario> executa(
      IniciarJogoRequest request,
      Runnable acaoParaQuandoUsuarioNaoExiste,
      Runnable acaoParaQuandoUsuarioNaoPodeJogar) {
    // 1
    var possivelUsuario = request.toUsuario(usuarioRepository);
    // 1
    if (possivelUsuario.isEmpty()) {
      acaoParaQuandoUsuarioNaoExiste.run();
      return Optional.empty();
    }
    // 1
    if (!possivelUsuario.get().podeJogar()) {
      acaoParaQuandoUsuarioNaoPodeJogar.run();
    }
    return possivelUsuario;
  }
}
