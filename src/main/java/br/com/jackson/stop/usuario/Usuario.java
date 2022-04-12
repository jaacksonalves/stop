package br.com.jackson.stop.usuario;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.Sala;
import org.springframework.util.Assert;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@ICP(3.5)
@Entity
@Table(name = "usuarios")
public class Usuario {

  // 0.5
  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  // 0.5
  private String nome;

  // 0.5
  private Integer nota;

  // 1
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Sala sala;

  public Usuario(String nome) {
    this.nome = nome;
  }

  /**
   * @deprecated (hibernate only)
   */
  @Deprecated(since = "1.0.0")
  public Usuario() {}

  public String getNome() {
    return nome;
  }

  public Sala getSala() {
    return sala;
  }

  public Integer getNota() {
    return nota;
  }

  public void setSala(Sala sala) {
    Assert.notNull(sala, "Sala não pode ser nula");
    Assert.state(this.podeJogar(), "Usuário já está em uma sala");
    Assert.state(sala.temVaga(), "Sala está lotada");

    this.sala = sala;
  }

  public boolean podeJogar() {
    // 1
    return this.sala == null;
  }
}
