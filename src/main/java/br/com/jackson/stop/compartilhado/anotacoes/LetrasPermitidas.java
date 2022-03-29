package br.com.jackson.stop.compartilhado.anotacoes;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * @author Jackson Alves: Verifica se uma coleção de Letras é válida, seguindo a regra > letras
 *     maiúsculas únicas de A a Z
 */
@Documented
@Constraint(validatedBy = {LetrasPermitidasValidator.class})
@Target({FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LetrasPermitidas {
  String message() default "Apenas letras maiúsculas únicas de A a Z são permitidas";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
