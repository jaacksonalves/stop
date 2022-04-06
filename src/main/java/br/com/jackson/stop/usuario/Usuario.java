package br.com.jackson.stop.usuario;

import br.com.jackson.stop.sala.Sala;
import org.springframework.util.Assert;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Long id;

  private String nome;

  private Integer nota;

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

  public void adicionaSala(Sala sala) {
    Assert.notNull(sala, "Sala não pode ser nula");
    Assert.state(this.podeJogar(), "Usuário já está em uma sala");

    this.sala = sala;
  }

  public boolean podeJogar() {
    return this.sala == null;
  }
}
