package api.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for {@link Password} annotation.
 * <ul>
 * <li>1. The minimum length of password should be 8 characters.</li>
 * <li>2. One special character like @,#,$,%</li>
 * <li>3. One upper case and one lower case</li>
 * <li>4. One numeric digit.</li>
 * </ul>
 * 
 * @see Password
 * 
 */
public class PasswordValidator implements ConstraintValidator<Password, String> {
    /**
     * Regular expression for password validation. The rules are:
     * <ul>
     * <li>1. Minimum 8 characters.</li>
     * <li>2. One special character like @,#,$,%</li>
     * <li>3. One upper case and one lower case</li>
     * <li>4. One numeric digit.</li>
     * </ul>
     * 
     * <pre>
     * (                       # Start of group
     *   (?=.*\d)              #   contains one digit from 0-9
     *   (?=.*[a-z])           #   contains one lowercase characters
     *   (?=.*[A-Z])           #   contains one uppercase characters
     *   (?=.*[@#$%])          #   contains one of @,#,$ or %
     *               .         #     match anything with previous condition checking
     *                 {8,}    #       length is at least 8 characters (no maximum) 
     * )                       # End of group
     * </pre>
     * <p>
     * <code>?=</code> means apply the assertion condition in combination with the others.
     * </p>
     * 
     */
    private String VALID_PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,})";

    /*
     * (non-Javadoc)
     * 
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation. Annotation)
     */
    public void initialize(Password password) {
        // Nothing to do
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    public boolean isValid(String str, ConstraintValidatorContext ctx) {
        return validate(str);
    }

    /**
     * Validates that the string is a valid password.
     * 
     * @param password
     *            The password to validate.
     * @return True if valid, false otherwise.
     */
    public boolean validate(final String password) {
        Pattern pattern = Pattern.compile(VALID_PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

}
