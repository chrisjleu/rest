package integration.service.auth;

import integration.api.model.user.auth.AuthenticationResponse;

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
    public AuthenticationResponse authenticate(String username, String password);
}
