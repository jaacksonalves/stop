package br.com.jackson.stop.sala;

public class SalasResponse {

  private final Long id;
  private final int numeroMaximoParticipantes;
  private final int numeroParticipantesAtual;
  private final int quantidadeCategorias;
  private final int quantidadeRodadas;
  private final boolean privada;

  public SalasResponse(Sala sala) {
    this.id = sala.getId();
    this.numeroMaximoParticipantes = sala.getMaximoJogadores();
    this.numeroParticipantesAtual = sala.getUsuarios().size();
    this.quantidadeCategorias = sala.getCategorias().size();
    this.quantidadeRodadas = sala.getRodadas();
    this.privada = sala.isPrivada();
  }

  public Long getId() {
    return id;
  }

  public int getNumeroMaximoParticipantes() {
    return numeroMaximoParticipantes;
  }

  public int getNumeroParticipantesAtual() {
    return numeroParticipantesAtual;
  }

  public int getQuantidadeCategorias() {
    return quantidadeCategorias;
  }

  public int getQuantidadeRodadas() {
    return quantidadeRodadas;
  }

  public boolean isPrivada() {
    return privada;
  }
}
