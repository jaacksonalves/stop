package br.com.jackson.stop.jogo.iniciar;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.Sala;
import br.com.jackson.stop.sala.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

// Alberto eu criei um service, não me demita

/**
 * @author jackson.mota Classe criada para realizar as operações de entrar num jogo relativos à Sala
 */
@Service
@ICP(7.5)
public class DetalhesSalaIniciarJogo {

  // 2.5
  private final SalaRepository salaRepository;
  private Optional<Sala> possivelSala;
  private HttpStatus status;
  private String mensagem;

  @Autowired
  public DetalhesSalaIniciarJogo(SalaRepository salaRepository) {
    this.salaRepository = salaRepository;
  }

  public DetalhesSalaIniciarJogo buscaSalaAleatoria() {
    // 1
    possivelSala =
        salaRepository.findAll().stream()
            // 1
            .filter(Sala::disponivelParaJogoAleatorio)
            .findFirst();
    // 1
    if (possivelSala.isEmpty()) {
      this.status = NOT_FOUND;
      this.mensagem = "Nenhuma sala disponível";
    }
    return this;
  }

  public DetalhesSalaIniciarJogo buscaSalaEspecifica(Long salaId, IniciarJogoRequest request) {
    possivelSala = salaRepository.findById(salaId);

    // 1
    if (possivelSala.isEmpty()) {
      this.status = NOT_FOUND;
      this.mensagem = "Sala não encontrada";
      return this;
    }
    // 1
    if (!possivelSala.get().temVaga()) {
      this.status = BAD_REQUEST;
      this.mensagem = "Sala não tem vaga";
      return this;
    }
    // 1
    if (!possivelSala.get().validaEntrada(request.getSenha())) {
      this.status = BAD_REQUEST;
      this.mensagem = "Senha inválida";
      return this;
    }
    return this;
  }

  public Optional<Sala> getPossivelSala() {
    return possivelSala;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getMensagem() {
    return mensagem;
  }
}
