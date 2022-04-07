package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.compartilhado.anotacoes.LetrasPermitidas;
import br.com.jackson.stop.usuario.Usuario;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "salas")
@ICP(9)
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
  // 0.5
  private String senha;
  // 0.5
  @Column(nullable = false)
  @ElementCollection
  @LetrasPermitidas
  @UniqueElements
  private List<String> letras;
  // 1
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TempoJogo tempoJogo;
  // 0.5
  @Column(nullable = false)
  private boolean privada = false;

  // 1
  @ManyToMany(cascade = {PERSIST, MERGE})
  private final List<Categoria> categorias = new ArrayList<>();

  // 1
  @OneToMany(
      mappedBy = "sala",
      cascade = {MERGE, PERSIST})
  @Size(max = 12)
  private final List<Usuario> usuarios = new ArrayList<>();
  // 0.5
  @Version private Integer versao;

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
    Assert.state(rodadas >= 4 && rodadas <= 12, "Rodadas devem estar entre 4 e 12");
    Assert.state(
        maximoJogadores >= 4 && maximoJogadores <= 12,
        "Maximo de jogadores devem estar entre 4 e 12");
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

  public List<Usuario> getUsuarios() {
    return usuarios;
  }

  // quando uma senha é setada, automaticamente a sala fica privada
  public void adicionaSenha(String senha) {
    Assert.state(this.senha == null, "Sala já possui senha cadastrada");
    Assert.state(StringUtils.hasText(senha), "Senha não pode ser nula ou vazia");

    this.senha = senha;
    this.privada = true;
  }

  public boolean temVaga() {
    // 1
    return this.usuarios.size() < this.maximoJogadores;
  }

  public boolean validaEntrada(String senha) {
    Assert.state(this.temVaga(), "Sala está cheia");
    // 2
    return !this.privada || this.senha.equals(senha);
  }

  public void adicionarUsuario(Usuario usuario) {
    Assert.state(this.temVaga(), "Sala está cheia");
    Assert.state(usuario.podeJogar(), "Usuario já está em uma sala");

    usuario.adicionaSala(this);
    this.usuarios.add(usuario);
  }

  /** verifica se a sala não é privada e se ainda tem espaço para entrar mais usuários */
  public boolean salaLivreEComVagaDisponivel() {
    // 2
    return !this.privada && this.temVaga();
  }

  public int getJogadoresAtuais() {
    return this.usuarios.size();
  }
}
