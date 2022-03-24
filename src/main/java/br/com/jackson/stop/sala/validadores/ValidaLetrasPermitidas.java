package br.com.jackson.stop.sala.validadores;

import br.com.jackson.stop.compartilhado.anotacoes.ICP;
import br.com.jackson.stop.sala.NovaSalaRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@ICP(3)
public class ValidaLetrasPermitidas implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return NovaSalaRequest.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    // 1
    if (errors.hasErrors()) return;

    // 1
    var request = (NovaSalaRequest) target;

    request
        .letras()
        .forEach(
            letra -> {
              // 1
              if (!letra.matches("^[A-Z]")) {
                errors.rejectValue(
                    // verificar porque não posso colocar o nome do campo aqui no primeiro
                    // parametro, ou da erro no teste: NotReadablePropertyException: Invalid
                    // property 'letras' of bean class [java.lang.Object]: Bean property 'java' is
                    // not
                    // readable or has an invalid getter method: Does the return type of the getter
                    // match the parameter type of the setter?,
                    // ps.: se eu testo via insomnia da certo, então é algo relacionado aos beans do
                    // Spring
                    null, "No campo Letras, apenas letras maiúsculas de A a Z são permitidas");
              }
            });
  }
}
