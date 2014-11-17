package integration.api.model.user.reg;

import integration.api.model.user.auth.AccountDao;
import lombok.Data;

/**
 * Encapsulates a failed or successful attempt to register a new user.
 * 
 * TODO: It's a wrapper around the {@link AccountDao} which doesn't make it very useful so it's not used currently. It
 * would be useful when/if there is more to capture about the result of a new user registration.
 */
@Data
public class NewUserRegistrationResult {

    AccountDao accountDao;
}
