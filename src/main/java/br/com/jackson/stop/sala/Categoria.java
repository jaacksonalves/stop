package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "categorias")
@ICP(1.0)
public class Categoria {

  // 0.5
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Long id;

  // 0.5
  @Column(nullable = false, unique = true)
  private String nome;

  /**
   * @deprecated (hibernate only)
   */
  @Deprecated(since = "1.0.0")
  public Categoria() {}

  public Categoria(@NotBlank String nome) {
    Assert.hasLength(nome, "Nome da categoria não pode ser vazio");
    this.nome = nome;
  }

  public String getNome() {
    return nome;
  }
}
