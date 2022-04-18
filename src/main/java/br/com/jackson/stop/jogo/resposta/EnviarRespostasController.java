package br.com.jackson.stop.jogo.resposta;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.SalaRepository;
import br.com.jackson.stop.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

@ICP(6)
@RestController
@RequestMapping("/api/respostas")
public class EnviarRespostasController {

  // 2
  private final SalaRepository salaRepository;
  private final UsuarioRepository usuarioRepository;

  @Autowired
  public EnviarRespostasController(
      SalaRepository salaRepository, UsuarioRepository usuarioRepository) {
    this.salaRepository = salaRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @Transactional
  @PostMapping("/{salaId}")
  // 1
  public void enviarRespostas(
      @PathVariable Long salaId, @RequestBody EnviarRespostasRequest request) {
    // 1
    var possivelSala = salaRepository.findById(salaId);
    Assert.state(possivelSala.isPresent(), "Não deveria chegar sala com Id errado aqui");
    var sala = possivelSala.get();

    // 1
    var possivelUsuario = usuarioRepository.findById(request.usuarioId());
    Assert.state(possivelUsuario.isPresent(), "Não deveria chegar usuario com Id errado aqui");
    var usuario = possivelUsuario.get();

    Assert.state(usuario.estaNaSala(sala), "Usuario não está na sala");

    Assert.state(
        sala.validaRespostasPertencemASala(request.respostas()),
        "Respostas não pertencem a sala, veriricar payload");

    //1
    if (usuario.jaRespondeu(sala)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario já respondeu pra essa sala");
    }
    sala.adicionaRespostas(request.respostas(), usuario);
  }
}
