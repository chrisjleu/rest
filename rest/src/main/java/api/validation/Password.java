package api.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ METHOD, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {
    /**
     * This method is used to provide a default message
     * 
     * @return
     */
    String message() default "{invalid. A valid password has one upper case char, one special char (any of @#$%), one number and a min of 8 chars}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}