package integration.service.auth;

import integration.api.model.apikey.AuthenticationRequest;
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

    /**
     * Authenticates a request to use the API.
     * 
     * @param request
     *            A {@link AuthenticationRequest} that encapsulates the pertinent information that was send in the HTTP
     *            request (i.e. the HTTP request to the API from the client).
     * @return The result of the authentication request.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request);
}
