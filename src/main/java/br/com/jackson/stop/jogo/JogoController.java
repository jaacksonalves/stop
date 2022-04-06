package br.com.jackson.stop.jogo;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.Sala;
import br.com.jackson.stop.sala.SalaRepository;
import br.com.jackson.stop.usuario.Usuario;
import br.com.jackson.stop.usuario.UsuarioRepository;
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
  private final UsuarioRepository usuarioRepository;

  @Autowired
  public JogoController(SalaRepository salaRepository, UsuarioRepository usuarioRepository) {
    this.salaRepository = salaRepository;
    this.usuarioRepository = usuarioRepository;
  }

  @PostMapping
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoAleatorio( // 1
      @Valid @RequestBody EntrarNoJogoRequest request) {
    var salaDisponivel =
        salaRepository.findAll().stream()
            .filter(Sala::salaLivreEComVagaDisponivel)
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Não há salas disponíveis"));
    // 1
    var usuario = request.toUsuario(usuarioRepository);

    this.validaSeUsuarioPodeJogar(usuario);

    this.validaEntradaNaSala(request, salaDisponivel);

    salaDisponivel.adicionarUsuario(usuario);

    // 1
    return new DetalhesDaSalaEmJogoResponse(salaDisponivel);
  }

  @PostMapping("/{salaId}")
  @Transactional
  public DetalhesDaSalaEmJogoResponse jogoEspecifico(
      @PathVariable Long salaId, @Valid @RequestBody EntrarNoJogoRequest request) {
    var sala =
        salaRepository
            .findById(salaId)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sala não encontrada"));

    var usuario = request.toUsuario(usuarioRepository);

    this.validaSeUsuarioPodeJogar(usuario);

    this.validaEntradaNaSala(request, sala);

    sala.adicionarUsuario(usuario);

    return new DetalhesDaSalaEmJogoResponse(sala);
  }

  private void validaEntradaNaSala(EntrarNoJogoRequest request, Sala sala) {
    Assert.notNull(request, "Entrada não pode ser nula");
    Assert.notNull(sala, "Sala não pode ser nula");
    // 1
    if (!sala.temVaga()) {
      throw new ResponseStatusException(BAD_REQUEST, "Sala cheia");
    }

    // 1
    if (!sala.validaEntrada(request.senha())) {
      throw new ResponseStatusException(
          BAD_REQUEST, "Não é possível entrar na sala, verifique a senha");
    }
  }

  private void validaSeUsuarioPodeJogar(Usuario usuario) {
    if (!usuario.podeJogar()) {
      throw new ResponseStatusException(BAD_REQUEST, "Usuário já está jogando em outra sala");
    }
  }
}
