package br.com.jackson.stop.jogo.entrar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.jogo.DetalhesDaSalaEmJogoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/jogo")
@ICP(7)
public class EntrarNoJogoController {

  // 2
  private final BuscaSalaParaEntrarNoJogo buscaSala;
  private final BuscaUsuarioParaEntrarNoJogo buscaUsuario;

  @Autowired
  public EntrarNoJogoController(
      BuscaSalaParaEntrarNoJogo buscaSala, BuscaUsuarioParaEntrarNoJogo buscaUsuario) {
    this.buscaSala = buscaSala;
    this.buscaUsuario = buscaUsuario;
  }

  // 1
  @PostMapping
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoAleatorio(
      @Valid @RequestBody EntrarNoJogoRequest request) {
    // 1
    var salaDisponivel =
        buscaSala
            .buscaSalaAleatoria()
            // 1
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Não há salas disponíveis"));

    // 1
    var usuario = buscaUsuario.getUsuario(request);

    salaDisponivel.adicionarUsuario(usuario);

    // 1
    return new DetalhesDaSalaEmJogoResponse(salaDisponivel);
  }

  @PostMapping("/{salaId}")
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoEspecifico(
      @PathVariable Long salaId, @Valid @RequestBody EntrarNoJogoRequest request) {
    var sala = buscaSala.buscaSalaEspecifica(salaId, request);

    var usuario = buscaUsuario.getUsuario(request);

    sala.adicionarUsuario(usuario);

    return new DetalhesDaSalaEmJogoResponse(sala);
  }
}
