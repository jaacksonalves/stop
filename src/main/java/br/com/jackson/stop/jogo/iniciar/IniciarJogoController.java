package br.com.jackson.stop.jogo.iniciar;

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
public class IniciarJogoController {

  // 2
  private final BuscaSalaParaIniciarJogo buscaSala;
  private final BuscaUsuarioParaIniciarJogo buscaUsuario;

  @Autowired
  public IniciarJogoController(
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
      @PathVariable Long salaId, @Valid @RequestBody IniciarJogoRequest request) {
    var sala = buscaSala.buscaSalaEspecifica(salaId, request);

    var usuario = buscaUsuario.getUsuario(request);

    sala.adicionarUsuario(usuario);

    return new DetalhesDaSalaEmJogoResponse(sala);
  }
}
