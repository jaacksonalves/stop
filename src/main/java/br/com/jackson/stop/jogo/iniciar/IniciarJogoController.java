package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.jogo.DetalhesDaSalaEmJogoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/jogo")
@ICP(9)
public class IniciarJogoController {

  // 2
  private final DetalhesSalaIniciarJogo buscaSala;
  private final BuscaUsuarioParaIniciarJogo buscaUsuario;

  @Autowired
  public IniciarJogoController(
      DetalhesSalaIniciarJogo buscaSala, BuscaUsuarioParaIniciarJogo buscaUsuario) {
    this.buscaSala = buscaSala;
    this.buscaUsuario = buscaUsuario;
  }

  // 1
  @PostMapping
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoAleatorio(
      @Valid @RequestBody IniciarJogoRequest request) {
    // 1
    var salaIniciarJogo = buscaSala.buscaSalaAleatoria();
    // 1
    var sala =
        salaIniciarJogo
            .getPossivelSala()
            .orElseThrow(
                // 1
                () ->
                    new ResponseStatusException(
                        salaIniciarJogo.getStatus(), salaIniciarJogo.getMensagem()));

    // 1
    var usuario = buscaUsuario.getUsuario(request);

    sala.adicionarUsuario(usuario);

    // 1
    return new DetalhesDaSalaEmJogoResponse(sala);
  }

  @PostMapping("/{salaId}")
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoEspecifico(
      @PathVariable Long salaId, @Valid @RequestBody IniciarJogoRequest request) {
    var salaIniciarJogo = buscaSala.buscaSalaEspecifica(salaId, request);

    var sala =
        salaIniciarJogo
            .getPossivelSala()
            .orElseThrow(
                // 1
                () ->
                    new ResponseStatusException(
                        salaIniciarJogo.getStatus(), salaIniciarJogo.getMensagem()));

    var usuario = buscaUsuario.getUsuario(request);

    sala.adicionarUsuario(usuario);

    return new DetalhesDaSalaEmJogoResponse(sala);
  }
}
