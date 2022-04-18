package br.com.jackson.stop.sala;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.compartilhado.anotacoes.PartialClass;
import org.springframework.util.Assert;

import java.util.Map;

/*
Aqui o ICP passou de 7, eu poderia criar uma outra Partial, vc quem vai me dizer rs
 */
@ICP(8)
@PartialClass(Sala.class)
public class SalaPartial1 {
  // 1
  private final Sala sala;

  public SalaPartial1(Sala sala) {
    Assert.notNull(sala, "Sala não pode ser nula");
    this.sala = sala;
  }

  /** verifica se a sala não é privada e se ainda tem espaço para entrar mais usuários */
  public boolean disponivelParaJogoAleatorio() {
    // 2
    return !this.sala.isPrivada() && this.sala.temVaga();
  }

  public boolean validaEntrada(String senha) {
    // 2
    return !this.sala.isPrivada() || this.sala.getSenha().equals(senha);
  }

  public boolean temVaga() {
    // 1
    return this.sala.getJogadoresAtuais() < this.sala.getInformacoesSala().getMaximoJogadores();
  }

  public boolean validaRespostasPertencemASala(Map<String, String> respostas) {
    var nomesCategorias = this.sala.getCategorias().stream().map(Categoria::getNome).toList();
    // 2
    return nomesCategorias.containsAll(respostas.keySet())
        && respostas.keySet().containsAll(nomesCategorias);
  }
}
