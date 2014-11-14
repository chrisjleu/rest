package integration.service.auth.service;

import integration.api.model.auth.AuthenticationResult;

/**
 * Provides services around user authentication.
 */
public interface AuthenticationService {

    /**
     * Authenticate the user given a username and password.
     * 
     * @param username
     * @param password
     * @return The result of the authentication request.
     */
    public AuthenticationResult authenticate(String username, String password);
}
