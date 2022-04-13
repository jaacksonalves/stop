package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.Sala;
import br.com.jackson.stop.sala.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Alberto eu criei um service, não me demita

/**
 * @author Jackson Alves: Classe criada para realizar as operações buscar uma Sala para iniciar um
 *     jogo
 */
@Service
@ICP(8)
public class BuscaSalaParaIniciarJogo {

  // 1
  private final SalaRepository salaRepository;

  @Autowired
  public BuscaSalaParaIniciarJogo(SalaRepository salaRepository) {
    this.salaRepository = salaRepository;
  }

  public Optional<Sala> buscaSalaAleatoria(Runnable acaoParaQuandoNaoHouverSala) {
    // 1
    var possivelSala =
        salaRepository.findAll().stream()
            // 1
            .filter(Sala::disponivelParaJogoAleatorio)
            .findFirst();
    //1
    if (possivelSala.isEmpty()) {
      acaoParaQuandoNaoHouverSala.run();
      return Optional.empty();
    }
    return possivelSala;
  }

  public Optional<Sala> buscaSalaEspecifica(
      Long salaId,
      // 1
      IniciarJogoRequest request,
      Runnable acaoParaSalaNaoEncontrada,
      Runnable acaoParaSalaSemVaga,
      Runnable acaoParaSenhaIncorreta) {

    var possivelSala = salaRepository.findById(salaId);

    // 1
    if (possivelSala.isEmpty()) {
      acaoParaSalaNaoEncontrada.run();
      return Optional.empty();
    }
    // 1
    if (!possivelSala.get().temVaga()) {
      acaoParaSalaSemVaga.run();
    }
    // 1
    if (!possivelSala.get().validaEntrada(request.getSenha())) {
      acaoParaSenhaIncorreta.run();
    }
    return possivelSala;
  }
}
