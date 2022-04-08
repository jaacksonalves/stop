package br.com.jackson.stop.jogo.entrar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.jogo.DetalhesDaSalaEmJogoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/jogo")
@ICP(7)
public class EntrarNoJogoController {

  // 2
  private final EntrarNoJogoSalaService salaService;
  private final EntrarNoJogoUsuarioService usuarioService;

  @Autowired
  public EntrarNoJogoController(
      EntrarNoJogoSalaService salaService, EntrarNoJogoUsuarioService usuarioService) {
    this.salaService = salaService;
    this.usuarioService = usuarioService;
  }

  // 1
  @PostMapping
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoAleatorio(
      @Valid @RequestBody EntrarNoJogoRequest request) {
    // 1
    var salaDisponivel = salaService.buscaSalaAleatoria();

    // 1
    var usuario = usuarioService.getUsuario(request);

    salaDisponivel.adicionarUsuario(usuario);

    // 1
    return new DetalhesDaSalaEmJogoResponse(salaDisponivel);
  }

  // 1
  @PostMapping("/{salaId}")
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoEspecifico(
      @PathVariable Long salaId, @Valid @RequestBody EntrarNoJogoRequest request) {
    var sala = salaService.buscaSalaEspecifica(salaId, request);

    var usuario = usuarioService.getUsuario(request);

    sala.adicionarUsuario(usuario);

    return new DetalhesDaSalaEmJogoResponse(sala);
  }
}
