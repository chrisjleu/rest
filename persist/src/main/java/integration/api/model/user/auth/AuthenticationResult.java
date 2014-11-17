package integration.api.model.user.auth;

import lombok.Data;

/**
 * Encapsulates a failed or successful user authentication attempt.
 */
@Data
public class AuthenticationResult {

    AccountDao account;
}
