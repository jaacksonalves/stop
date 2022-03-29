package br.com.jackson.stop.compartilhado.anotacoes;

import br.com.jackson.stop.sala.NovaSalaRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class LetrasCompativeisRodadasValidator
    implements ConstraintValidator<LetrasCompativeisRodadas, NovaSalaRequest> {

  @Override
  public boolean isValid(NovaSalaRequest request, ConstraintValidatorContext context) {
    return request.letras().size() >= request.rodadas();
  }
}
