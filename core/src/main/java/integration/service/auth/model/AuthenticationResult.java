package integration.service.auth.model;

import lombok.Data;

/**
 * Encapsulates a failed or successful user authentication attempt.
 */
@Data
public class AuthenticationResult {

    Account account;
}
