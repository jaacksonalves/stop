package br.com.jackson.stop.compartilhado.anotacoes;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

@Documented
@Constraint(validatedBy = {LetrasCompativeisRodadasValidator.class})
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LetrasCompativeisRodadas {

  String message() default "A quantidade de letras deve ser maior ou igual ao número de rodadas";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
