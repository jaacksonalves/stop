package br.com.jackson.stop.sala;

import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class InformacoesSala {

  // 0.5
  @Column(nullable = false)
  @Range(min = 1, max = 12)
  private int rodadas;
  // 0.5
  @Column(nullable = false)
  @Range(min = 1, max = 12)
  private int maximoJogadores;

  // 1
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TempoJogo tempoJogo;

  public InformacoesSala(int rodadas, int maximoJogadores, TempoJogo tempoJogo) {
    this.rodadas = rodadas;
    this.maximoJogadores = maximoJogadores;
    this.tempoJogo = tempoJogo;
  }

  public InformacoesSala() {
  }

  public int getRodadas() {
    return rodadas;
  }

  public int getMaximoJogadores() {
    return maximoJogadores;
  }

  public TempoJogo getTempoJogo() {
    return tempoJogo;
  }
}
