package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import org.springframework.util.Assert;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "salas")
@ICP(6.0)
public class Sala {

  // 0.5
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(nullable = false)
  private Long id;

  // 0.5
  @Column(nullable = false)
  private int rodadas;

  // 0.5
  @Column(nullable = false)
  private int maximoJogadores;

  // 0.5
  private String senha;

  // 1
  @ManyToMany private final List<Categoria> categorias = new ArrayList<>();

  // 0.5
  @Column(nullable = false)
  @ElementCollection
  private List<String> letras;

  // 1
  @Column(nullable = false)
  private TempoJogo tempoJogo;

  // 0.5
  @Column(nullable = false)
  private boolean privada = false;

  /**
   * @deprecated (hibernate only)
   */
  @Deprecated(since = "1.0.0")
  public Sala() {}

  public Sala(
      @NotNull @Positive @Max(12) int rodadas,
      @NotNull @Positive @Max(12) int maximoJogadores,
      String senha,
      @NotNull @NotEmpty List<Categoria> categorias,
      @NotNull @NotEmpty List<String> letras,
      @NotNull TempoJogo tempoJogo) {
    Assert.state(rodadas >= 1 && rodadas <= 12, "Rodadas devem estar entre 1 e 12");
    Assert.state(
        maximoJogadores >= 1 && maximoJogadores <= 12,
        "Maximo de jogadores devem estar entre 1 e 12");
    Assert.notNull(categorias, "Categorias não pode ser nula");
    Assert.state(!categorias.isEmpty(), "Categorias não pode ser nula");
    Assert.notNull(letras, "Letras não pode ser nula");
    Assert.state(!letras.isEmpty(), "Letras não pode ser nula");
    Assert.state(
        letras.size() >= categorias.size(),
        "A quantidade de letras deve ser maior ou igual ao número de rodadas");
    Assert.notNull(tempoJogo, "Tempo de jogo nao pode ser nulo");

    // 1
    if (senha != null) {
      this.privada = true;
    }
    this.rodadas = rodadas;
    this.maximoJogadores = maximoJogadores;
    this.senha = senha;
    this.categorias.addAll(categorias);
    this.letras = letras;
    this.tempoJogo = tempoJogo;
  }

  public Long getId() {
    return id;
  }
}
