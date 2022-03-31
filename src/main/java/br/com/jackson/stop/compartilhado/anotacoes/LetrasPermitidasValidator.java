package br.com.jackson.stop.compartilhado.anotacoes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * @author Jackson Alves: Verifica se uma coleção de Letras é válida, seguindo a regra > letras
 *     maiúsculas únicas de A a Z
 */
@ICP(1)
public class LetrasPermitidasValidator
    implements ConstraintValidator<LetrasPermitidas, Collection<String>> {

  @Override
  public boolean isValid(Collection<String> lista, ConstraintValidatorContext context) {
    // 1
    return lista.stream().allMatch(s -> s.matches("^[A-Z]"));
  }
}
