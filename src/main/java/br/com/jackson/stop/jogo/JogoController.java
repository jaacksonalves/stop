package br.com.jackson.stop.jogo;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.Sala;
import br.com.jackson.stop.sala.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/jogo")
@ICP(7)
public class JogoController {

  // 1
  private final SalaRepository salaRepository;

  @Autowired
  public JogoController(SalaRepository salaRepository) {
    this.salaRepository = salaRepository;
  }

  // fiquei na dúvida se é Post ou Get, ao mesmo tempo que buscamos os dados da sala, criamos um
  // usuário e temos um json no corpo
  @GetMapping
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoAleatorio( // 1
      @Valid @RequestBody EntrarNoJogoRequest request) {
    var salaDisponivel =
        salaRepository.findAll().stream()
            .filter(sala -> sala.salaComVagaDisponivel() && !sala.isPrivada())
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Não há salas disponíveis"));

    validaEntradaNaSala(request, salaDisponivel);

    // 1
    var usuario = request.toUsuario();

    salaDisponivel.adicionarUsuario(usuario);

    // 1
    return new DetalhesDaSalaEmJogoResponse(salaDisponivel);
  }

  @GetMapping("/{salaId}")
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoEspecifico(
      @PathVariable Long salaId, @Valid @RequestBody EntrarNoJogoRequest request) {
    var sala =
        salaRepository
            .findById(salaId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sala não encontrada"));

    validaEntradaNaSala(request, sala);

    var usuario = request.toUsuario();

    sala.adicionarUsuario(usuario);

    return new DetalhesDaSalaEmJogoResponse(sala);
  }

  private void validaEntradaNaSala(EntrarNoJogoRequest request, Sala sala) {
    Assert.notNull(request, "Entrada não pode ser nula");
    Assert.notNull(sala, "Sala não pode ser nula");

    // 1
    if (!sala.validaEntrada(request)) {
      throw new ResponseStatusException(
          BAD_REQUEST, "Não é possível entrar na sala, verifique a senha");
    }
  }
}
