package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.compartilhado.anotacoes.LetrasPermitidas;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "salas")
@ICP(7)
public class Sala {

  // 0.5
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(nullable = false)
  private Long id;

  // 0.5
  @Column(nullable = false)
  @Range(min = 1, max = 12)
  private int rodadas;

  // 0.5
  @Column(nullable = false)
  @Range(min = 1, max = 12)
  private int maximoJogadores;

  @Max(12)
  private Integer jogadoresAtuais = 0;

  // 0.5
  private String senha;

  // 1
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private final List<Categoria> categorias = new ArrayList<>();

  // 0.5
  @Column(nullable = false)
  @ElementCollection
  @LetrasPermitidas
  @UniqueElements
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
      @NotNull @Range(min = 4, max = 12) int rodadas,
      @NotNull @Range(min = 4, max = 12) int maximoJogadores,
      @NotNull @NotEmpty @UniqueElements List<Categoria> categorias,
      @NotNull @NotEmpty @LetrasPermitidas @UniqueElements List<String> letras,
      @NotNull TempoJogo tempoJogo) {
    Assert.state(rodadas >= 4 && rodadas <= 12, "Rodadas devem estar entre 1 e 12");
    Assert.state(
        maximoJogadores >= 4 && maximoJogadores <= 12,
        "Maximo de jogadores devem estar entre 1 e 12");
    Assert.notNull(categorias, "Categorias não pode ser nula");
    Assert.state(!categorias.isEmpty(), "Categorias não pode ser nula");
    Assert.notNull(letras, "Letras não pode ser nula");
    Assert.state(!letras.isEmpty(), "Letras não pode ser nula");
    Assert.state(
        letras.size() >= rodadas,
        "A quantidade de letras deve ser maior ou igual ao número de rodadas");
    Assert.notNull(tempoJogo, "Tempo de jogo nao pode ser nulo");

    this.rodadas = rodadas;
    this.maximoJogadores = maximoJogadores;
    this.categorias.addAll(categorias);
    this.letras = letras;
    this.tempoJogo = tempoJogo;
  }

  public int getRodadas() {
    return rodadas;
  }

  public int getMaximoJogadores() {
    return maximoJogadores;
  }

  public String getSenha() {
    return senha;
  }

  public List<Categoria> getCategorias() {
    return categorias;
  }

  public List<String> getLetras() {
    return letras;
  }

  public TempoJogo getTempoJogo() {
    return tempoJogo;
  }

  public boolean isPrivada() {
    return privada;
  }

  public Long getId() {
    return id;
  }

  public Integer getJogadoresAtuais() {
    return jogadoresAtuais;
  }

  // quando uma senha é setada, automaticamente a sala fica privada
  public void adicionaSenha(String senha) {
    Assert.state(this.senha == null, "Sala já possui senha cadastrada");
    Assert.state(StringUtils.hasText(senha), "Senha não pode ser nula ou vazia");

    this.senha = senha;
    this.privada = true;
  }

  public boolean salaDisponivel() {
    //1
    return this.jogadoresAtuais < this.maximoJogadores;
  }
}
