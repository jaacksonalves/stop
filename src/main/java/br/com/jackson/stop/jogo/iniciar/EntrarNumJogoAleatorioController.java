package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.jogo.DetalhesDaSalaEmJogoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/jogo")
/*
Não deu pra deixar dentro do limite de 7 do CDD, vamos continuar tentando?
 */
@ICP(8)
public class EntrarNumJogoAleatorioController {

  // 2
  private final BuscaSalaParaIniciarJogo buscaSala;
  private final BuscaUsuarioParaIniciarJogo buscaUsuario;

  @Autowired
  public EntrarNumJogoAleatorioController(
      BuscaSalaParaIniciarJogo buscaSala, BuscaUsuarioParaIniciarJogo buscaUsuario) {
    this.buscaSala = buscaSala;
    this.buscaUsuario = buscaUsuario;
  }

  // 1
  @PostMapping
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoAleatorio(
      @Valid @RequestBody IniciarJogoRequest request) {
    // 1
    var possivelSala =
        buscaSala.buscaSalaAleatoria(
            jogaResponseStatusException(NOT_FOUND, "Não há salas disponíveis"));

    Assert.state(
        possivelSala.isPresent(),
        "Era pra ter salas disponiveis aqui ou não ter chegado nesse ponto pois uma exceção foi lançada");

    // 1
    var usuario =
        buscaUsuario.executa(
            request,
            // 2
            jogaResponseStatusException(NOT_FOUND, "Usuário não existe"),
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
