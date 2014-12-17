package integration.service.auth.stormpath;

import integration.api.model.apikey.ApiToken;
import integration.api.model.apikey.AuthenticationRequest;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.service.auth.AuthenticationService;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.http.HttpMethod;
import com.stormpath.sdk.http.HttpRequest;
import com.stormpath.sdk.http.HttpRequests;
import com.stormpath.sdk.oauth.AccessTokenResult;
import com.stormpath.sdk.oauth.OauthAuthenticationResult;
import com.stormpath.sdk.oauth.TokenResponse;
import com.stormpath.sdk.resource.ResourceException;

@Service
public class StormpathAuthenticationService extends AbstractStormpathService implements AuthenticationService {

    Logger logger = LoggerFactory.getLogger(StormpathAuthenticationService.class);

    public StormpathAuthenticationService(StormpathClientFactory factory, String applicationName) {
        super(factory, applicationName);
    }

    @Override
    public AuthenticationResponse authenticate(String username, String password) {
        AuthenticationResponse response = new AuthenticationResponse();

        UsernamePasswordRequest request = new UsernamePasswordRequest(username, password);
        try {
            AuthenticationResult result = getApplication().authenticateAccount(request);
            Account account = result.getAccount();
            AccountDao accountDao = new AccountDao(account.getUsername(), "alias");
            accountDao.setId(account.getHref());
            accountDao.setEmail(account.getEmail());
            response.setAccount(accountDao);
        } catch (ResourceException e) {
            logger.error("Unable to authenticate user", e);
        }

        return response;
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        return authenticate(buildHttpRequest(request));
    }

    private AuthenticationResponse authenticate(HttpRequest request) {
        AuthenticationResponse response = new AuthenticationResponse();
        try {
            OauthAuthenticationResult result = (OauthAuthenticationResult) getApplication().authenticateOauthRequest(
                    request).execute();

            Account account = result.getAccount();
            AccountDao accountDao = new AccountDao(account.getUsername(), "alias");
            accountDao.setId(account.getHref());
            accountDao.setEmail(account.getEmail());
            response.setAccount(accountDao);

        } catch (ResourceException e) {
            logger.error("Unable to authenticate user", e);
        }

        return response;
    }

    @Override
    public ApiToken authenticateForToken(AuthenticationRequest request) {
        AccessTokenResult result = (AccessTokenResult) getApplication().authenticateOauthRequest(request).execute();
        
        TokenResponse token = result.getTokenResponse();
        
        ApiToken apiToken = new ApiToken();
        apiToken.setAccessToken(token.getAccessToken());
        apiToken.setExpiresIn(token.getExpiresIn());
        apiToken.setTokenType(token.getTokenType());
        apiToken.setRefreshToken(token.getRefreshToken());
        apiToken.setScope(token.getScope());
        
        return apiToken;
    }

    /**
     * Constructs a {@link HttpRequest} that Stormpath needs in order to do the authentication. The Stormpath API also
     * works if you directly give it a <code>HttpServletRequst</code> object but since that is not accessible from this
     * layer we have effectively just rebuild it here from the input parameters).
     * 
     * @param AuthenticationRequest
     *            request A {@link AuthenticationRequest}
     * @return A {@link HttpRequest} that Stormpath uses to authenticate.
     */
    HttpRequest buildHttpRequest(AuthenticationRequest request) {
        return HttpRequests.method(HttpMethod.fromName(request.getHttpRequestMethod()))
                .headers(request.getHttpRequestHeaders()).queryParameters(request.getHttpQueryString()).build();
    }

    /**
     * Constructs a {@link HttpRequest} that Stormpath needs in order to do the authentication. The Stormpath API also
     * works if you directly give it a <code>HttpServletRequst</code> object but since that is not accessible from this
     * layer we have effectively just rebuild it here from the input parameters).
     * 
     * @param method
     *            The HTTP method of the original request as a String.
     * @param headers
     *            A map of headers (some headers can have the same name but with different values hence it's a map of
     *            <code>String[]</code> as opposed to just a map of <code>String</code>s.
     * @param queryString
     *            The request query string.
     * @return A {@link HttpRequest} that Stormpath uses to authenticate.
     */
    HttpRequest buildHttpRequest(String method, Map<String, String[]> headers, String queryString) {
        return HttpRequests.method(HttpMethod.fromName(method)).headers(headers).queryParameters(queryString).build();
    }

}
