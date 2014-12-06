package api.validation;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * Tests password validation.
 * 
 * @see PasswordValidator
 */
public class PasswordValidatorTest {

    private PasswordValidator passwordValidator = new PasswordValidator();

    private static final boolean VALID = true;

    private static final boolean INVALID = false;

    @Test
    public void validation_succeeds() {
        assertThat(passwordValidator.validate("p@ssw0rD"), is(VALID));
    }

    @Test
    public void validation_fails_on_password_too_short() {
        assertThat(passwordValidator.validate("f@i1ure"), is(INVALID));
    }

    @Test
    public void validation_fails_on_password_without_special_character() {
        assertThat(passwordValidator.validate("n0 special characteR"), is(INVALID));
    }

    @Test
    public void validation_fails_on_password_without_lower_case_char() {
        assertThat(passwordValidator.validate("N0 LOWER CASE CH@RACTER"), is(INVALID));
    }

    @Test
    public void validation_fails_on_password_without_upper_case_char() {
        assertThat(passwordValidator.validate("n0 upper case ch@racter"), is(INVALID));
    }

    @Test
    public void validation_fails_on_password_without_numeric_char() {
        assertThat(passwordValidator.validate("no numeric ch@racteR"), is(INVALID));
    }
}