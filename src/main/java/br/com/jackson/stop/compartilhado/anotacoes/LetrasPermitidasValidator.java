package br.com.jackson.stop.compartilhado.anotacoes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

/**
 * @author Jackson Alves: Verifica se uma coleção de Letras é válida, seguindo a regra > letras
 *     maiúsculas únicas de A a Z
 */
@ICP(4)
public class LetrasPermitidasValidator
    implements ConstraintValidator<LetrasPermitidas, Collection<String>> {

  @Override
  public boolean isValid(Collection<String> lista, ConstraintValidatorContext context) {
    // 1
    if (lista == null) return false;
    // 1
    if (lista.isEmpty()) return false;
    // 1
    if (this.itensDuplicados(lista)) return false;

    return lista.stream().allMatch(s -> s.matches("^[A-Z]"));
  }

  /** Verifica se existem itens duplicados na lista. */
  private boolean itensDuplicados(Collection<String> lista) {
    // 1
    return lista.stream().distinct().count() != lista.size();
  }
}
