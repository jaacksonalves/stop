package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

@ICP(4.5)
public class IniciarJogoRequest {

  // 1.5
  @NotBlank private String nomeUsuario;
  private String senha;
  private Long idUsuario;

  public IniciarJogoRequest(String nomeUsuario) {
    this.nomeUsuario = nomeUsuario;
  }

  public IniciarJogoRequest() {}

  public String getSenha() {
    return senha;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }

  public String getNomeUsuario() {
    return nomeUsuario;
  }

  // adicionei o Id do usuário pra simular um usuário já cadastrado anteriormente e enviando o seu
  // token
  // 2
  public Optional<Usuario> toUsuario(UsuarioRepository usuarioRepository) {
    // 1
    if (idUsuario == null) {
      return Optional.of(new Usuario(nomeUsuario));
    }
    return usuarioRepository.findById(idUsuario);
  }
}
