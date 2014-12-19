package integration.service.auth;

import integration.api.model.apikey.ApiToken;
import integration.api.model.apikey.AuthenticationRequest;
import integration.api.model.user.auth.AuthenticationResponse;

/**
 * Provides services around user authentication.
 */
public interface AuthenticationService {

    /**
     * Authenticate the user given a username and password. Useful mainly for basic authentication only.
     * 
     * @param username
     * @param password
     * @return The result of the authentication request.
     */
    public AuthenticationResponse authenticate(String username, String password);

    /**
     * Authenticates a user.
     * 
     * @param request
     *            A {@link AuthenticationRequest} that encapsulates the pertinent information that was send in the HTTP
     *            request (i.e. the HTTP request to the API from the client).
     * @return The result of the authentication request.
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request);

    /**
     * Authenticates a user and a token is returned in exchange for the login credentials (i.e. the access key and
     * secret). An access key and secret obviously must have been obtained prior to this by some other means.
     * 
     * @param accessKey
     * @param secret
     * @return
     */
    public ApiToken authenticateForToken(String accessKey, String secret);

}
