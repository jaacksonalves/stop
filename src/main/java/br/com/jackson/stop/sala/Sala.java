package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.compartilhado.anotacoes.LetrasPermitidas;
import br.com.jackson.stop.jogo.resposta.Resposta;
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
import java.util.Map;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "salas")
@ICP(8)
public class Sala {

  // 0.5
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(nullable = false)
  private Long id;

  // 1
  @Embedded private InformacoesSala informacoesSala;

  // 0.5
  private String senha;
  // 0.5
  @Column(nullable = false)
  @ElementCollection
  @LetrasPermitidas
  @UniqueElements
  private List<String> letras;

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
  @OneToMany(
      mappedBy = "sala",
      cascade = {MERGE, PERSIST})
  private final List<Resposta> respostas = new ArrayList<>();

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

    this.informacoesSala = new InformacoesSala(rodadas, maximoJogadores, tempoJogo);
    this.categorias.addAll(categorias);
    this.letras = letras;
  }

  public InformacoesSala getInformacoesSala() {
    return informacoesSala;
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
    return new SalaPartial1(this).temVaga();
  }

  public void adicionarUsuario(Usuario usuario) {
    Assert.state(this.temVaga(), "A sala não deveria estar cheia nesse ponto do código");
    Assert.state(usuario.podeJogar(), "O Usuário deveria poder jogar nesse ponto do código");

    usuario.setSala(this);
    this.usuarios.add(usuario);
  }

  /** verifica se a sala não é privada e se ainda tem espaço para entrar mais usuários */
  public boolean disponivelParaJogoAleatorio() {
    return new SalaPartial1(this).disponivelParaJogoAleatorio();
  }

  public int getJogadoresAtuais() {
    return this.usuarios.size();
  }

  public boolean validaEntrada(String senha) {
    return new SalaPartial1(this).validaEntrada(senha);
  }

  public boolean validaRespostasPertencemASala(Map<String, String> respostas) {
    return new SalaPartial1(this).validaRespostasPertencemASala(respostas);
  }

  public void adicionaRespostas(Map<String, String> respostas, Usuario usuario) {
    Assert.notNull(usuario, "Usuário não pode ser nulo");
    Assert.notNull(respostas, "Respostas não podem ser nulas");
    Assert.state(this.validaRespostasPertencemASala(respostas), "Respostas não pertencem a sala");
    this.respostas.add(new Resposta(this, usuario, respostas));
  }

  public boolean contemRespostasParaUsuario(Usuario usuario) {
    // 1
    return this.respostas.stream().anyMatch(r -> r.existeRespostaParaUsuario(usuario));
  }
}
