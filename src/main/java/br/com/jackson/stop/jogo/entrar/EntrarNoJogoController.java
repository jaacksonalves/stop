package br.com.jackson.stop.jogo.entrar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.jogo.DetalhesDaSalaEmJogoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/jogo")
@ICP(6)
public class EntrarNoJogoController {

  // 1
  private final EntrarNoJogoService entrarNoJogoService;

  @Autowired
  public EntrarNoJogoController(EntrarNoJogoService entrarNoJogoService) {
    this.entrarNoJogoService = entrarNoJogoService;
  }

  // 1
  @PostMapping
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoAleatorio(
      @Valid @RequestBody EntrarNoJogoRequest request) {
    // 1
    var salaDisponivel = entrarNoJogoService.buscaSalaAleatoria();

    // 1
    var usuario = entrarNoJogoService.getUsuario(request);

    salaDisponivel.adicionarUsuario(usuario);

    // 1
    return new DetalhesDaSalaEmJogoResponse(salaDisponivel);
  }

  // 1
  @PostMapping("/{salaId}")
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoEspecifico(
      @PathVariable Long salaId, @Valid @RequestBody EntrarNoJogoRequest request) {
    var sala = entrarNoJogoService.buscaSalaEspecifica(salaId, request);

    var usuario = entrarNoJogoService.getUsuario(request);

    sala.adicionarUsuario(usuario);

    return new DetalhesDaSalaEmJogoResponse(sala);
  }
}
