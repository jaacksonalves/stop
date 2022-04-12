package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.Sala;
import br.com.jackson.stop.sala.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

// Alberto eu criei um service, não me demita

/**
 * @author jackson.mota Classe criada para realizar as operações de entrar num jogo relativos à Sala
 */
@Service
@ICP(6)
public class BuscaSalaParaIniciarJogo {

  // 1
  private final SalaRepository salaRepository;

  @Autowired
  public BuscaSalaParaIniciarJogo(SalaRepository salaRepository) {
    this.salaRepository = salaRepository;
  }

  public Optional<Sala> buscaSalaAleatoria() {
    // 1
    return salaRepository.findAll().stream()
        // 1
        .filter(Sala::disponivelParaJogoAleatorio)
        .findFirst();
  }

  public Sala buscaSalaEspecifica(Long salaId, IniciarJogoRequest request) {
    var sala =
        salaRepository
            .findById(salaId)
            // 1
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Sala não encontrada"));

    // 1
    if (!sala.temVaga()) {
      throw new ResponseStatusException(BAD_REQUEST, "Sala não tem vaga");
    }
    // 1
    if (!sala.validaEntrada(request.getSenha())) {
      throw new ResponseStatusException(BAD_REQUEST, "Senha inválida");
    }

    return sala;
  }
}
