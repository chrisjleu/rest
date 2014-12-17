package integration.service.auth.dummy;

import integration.api.model.apikey.ApiToken;
import integration.api.model.apikey.AuthenticationRequest;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.service.auth.AuthenticationService;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Here before a real implementation is decided upon.
 */
@Service("dummyAuthService")
@Profile("standalone")
public class DummyAuthenticationService implements AuthenticationService {

    public AuthenticationResponse authenticate(String username, String password) {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        if ("name".equals(username) && "pwd".equals(password)) {
            AccountDao accountDao = new AccountDao(username, "alias");
            accountDao.setId(UUID.randomUUID().toString());
            accountDao.setEmail(username.concat("@email.com"));
            authenticationResponse.setAccount(accountDao);
        }

        return authenticationResponse;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();

        String token = getBearerToken(request);
        // TODO complete dummy bearer token authentication (for when the app wants to access the api)

        return authenticationResponse;
    }

    private String getBearerToken(AuthenticationRequest request) {
        String authorizationHeader = getAuthorizationHeader(request);
        // TODO parse header to get the token
        return null;
    }

    private String getAuthorizationHeader(AuthenticationRequest request) {
        if (request.getHttpRequestHeaders() != null) {
            String[] authorizationHeader = request.getHttpRequestHeaders().get("Authorization");
            if (authorizationHeader != null && authorizationHeader.length > 0) {
                return authorizationHeader[0];
            }
        }

        return null;
    }

    @Override
    public ApiToken authenticateForToken(AuthenticationRequest request) {
        ApiToken token = new ApiToken();
        token.setAccessToken("DUMMY-TOKEN:DSK434jnke894jskjlkhjf98s");
        token.setTokenType("bearer");
        token.setExpiresIn("3600");
        return token;
    }
}
