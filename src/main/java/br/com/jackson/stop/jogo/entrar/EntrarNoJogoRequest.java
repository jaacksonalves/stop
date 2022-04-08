package br.com.jackson.stop.jogo.entrar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;

@ICP(4.5)
public class EntrarNoJogoRequest {

  // 1.5
  @NotBlank private String nomeJogador;
  private String senha;
  private Long idUsuario;

  public EntrarNoJogoRequest(String nomeJogador) {
    this.nomeJogador = nomeJogador;
  }

  public EntrarNoJogoRequest() {}

  public String getNomeJogador() {
    return nomeJogador;
  }

  public String getSenha() {
    return senha;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public void setSenha(String senha) {
    this.senha = senha;
  }

  // adicionei o Id do usuário pra simular um usuário já cadastrado anteriormente e enviando o seu
  // token
  // 2
  public Usuario toUsuario(UsuarioRepository usuarioRepository) {
    // 1
    if (idUsuario == null) {
      return new Usuario(nomeJogador);
    }
    return usuarioRepository
        .findById(idUsuario)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
  }
}