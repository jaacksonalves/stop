package br.com.jackson.stop.jogo.resposta;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.Sala;
import br.com.jackson.stop.usuario.Usuario;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Map;

@ICP(4)
@Entity
@Table(name = "respostas")
public class Resposta {

  // 0.5
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  private Long id;
  // 1
  @ManyToOne @NotNull private Sala sala;
  // 1
  @ManyToOne @NotNull private Usuario usuario;
  // 0.5
  @ElementCollection
  @NotNull
  @MapKeyColumn(name = "categoria")
  @Column(name = "resposta")
  private Map<String, String> respostasInformadas;

  public Resposta(Sala sala, Usuario usuario, Map<String, String> respostasInformadas) {
    Assert.notNull(sala, "Sala não pode ser nula");
    Assert.notNull(usuario, "Usuário não pode ser nulo");
    Assert.notNull(respostasInformadas, "Respostas não podem ser nulas");
    Assert.state(
        sala.validaRespostasPertencemASala(respostasInformadas), "Respostas não pertencem a sala");

    this.sala = sala;
    this.usuario = usuario;
    this.respostasInformadas = respostasInformadas;
  }

  /**
   * @deprecated (hibernate only)
   */
  @Deprecated(since = "1.0.0")
  public Resposta() {}

  public Long getId() {
    return id;
  }

  public Sala getSala() {
    return sala;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public Map<String, String> getRespostasInformadas() {
    return respostasInformadas;
  }

  public boolean existeRespostaParaUsuario(Usuario usuario) {
    Assert.notNull(usuario, "Usuário não pode ser nulo");
    // 1
    return usuario.equals(this.usuario);
  }
}
