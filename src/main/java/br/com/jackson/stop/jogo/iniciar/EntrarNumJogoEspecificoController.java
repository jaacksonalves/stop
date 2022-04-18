package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/jogo")
/*
Não deu pra deixar dentro do limite de 7 do CDD menos ainda, vamos continuar tentando?
 */
@ICP(10)
public class EntrarNumJogoEspecificoController {

  // 2
  private final BuscaSalaParaIniciarJogo buscaSala;
  private final BuscaUsuarioParaIniciarJogo buscaUsuario;

  @Autowired
  public EntrarNumJogoEspecificoController(
      BuscaSalaParaIniciarJogo buscaSala, BuscaUsuarioParaIniciarJogo buscaUsuario) {
    this.buscaSala = buscaSala;
    this.buscaUsuario = buscaUsuario;
  }

  // 1
  @PostMapping("/{salaId}")
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoAleatorio(
      @PathVariable Long salaId, @Valid @RequestBody IniciarJogoRequest request) {
    // 1
    var possivelSala =
        buscaSala.buscaSalaEspecifica(
            salaId,
            request,
            // 3
            jogaResponseStatusException(NOT_FOUND, "Sala não encontrada"),
            jogaResponseStatusException(BAD_REQUEST, "Sala não tem vaga disponível"),
            jogaResponseStatusException(BAD_REQUEST, "Senha incorreta"));

    Assert.state(
        possivelSala.isPresent(),
        "Sala deveria estar presente ou não chegar nesse ponto do código pois exceção foi lançada");

    var usuario =
        buscaUsuario.executa(
            request,
            // 2
            jogaResponseStatusException(NOT_FOUND, "Não há usuários disponíveis"),
            jogaResponseStatusException(BAD_REQUEST, "Usuário já está em um jogo"));

    Assert.state(
        usuario.isPresent(),
        "Usuário deveria estar presente ou não ter chegado nesse ponto pois uma exceção foi lançada");

    possivelSala.get().adicionarUsuario(usuario.get());

    // 1
    return new DetalhesDaSalaEmJogoResponse(possivelSala.get());
  }

  private Runnable jogaResponseStatusException(HttpStatus status, String mensagem) {
    return () -> {
      throw new ResponseStatusException(status, mensagem);
    };
  }
}
