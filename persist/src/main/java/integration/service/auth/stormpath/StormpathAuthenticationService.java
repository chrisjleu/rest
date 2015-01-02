package integration.service.auth.stormpath;

import integration.api.model.apikey.ApiToken;
import integration.api.model.apikey.AuthenticationRequest;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.service.auth.AuthenticationService;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;

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
public class StormpathAuthenticationService implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(StormpathAuthenticationService.class);

    private StormpathClientHelper stormpathClientHelper;

    @Inject
    StormpathAuthenticationService(StormpathClientHelper stormpathClientHelper) {
        this.stormpathClientHelper = stormpathClientHelper;
    }

    @Override
    public AuthenticationResponse authenticate(String username, String password) {
        AuthenticationResponse response = new AuthenticationResponse();

        UsernamePasswordRequest request = new UsernamePasswordRequest(username, password);
        try {
            AuthenticationResult result = stormpathClientHelper.getApplication().authenticateAccount(request);
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

    AuthenticationResponse authenticate(HttpRequest request) {
        AuthenticationResponse response = new AuthenticationResponse();
        try {
            OauthAuthenticationResult result = (OauthAuthenticationResult) stormpathClientHelper.getApplication()
                    .authenticateOauthRequest(request).execute();

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
    public ApiToken authenticateForToken(String accessKey, String secret) {

        logger.debug("Obtaining access token for application {} given key {}",
                stormpathClientHelper.getApplicationName(), accessKey);

        HttpRequest tokenRequest = buildHttpRequest(accessKey, secret);

        // This makes the request to Stormpath to get the API access (bearer) token
        // TODO the TTL should be configurable
        AccessTokenResult result = (AccessTokenResult) stormpathClientHelper.getApplication()
                .authenticateOauthRequest(tokenRequest).withTtl(3600).execute();

        TokenResponse token = result.getTokenResponse();

        return buildApiToken(token);
    }

    ApiToken buildApiToken(TokenResponse token) {
        ApiToken apiToken = new ApiToken();
        apiToken.setAccessToken(token.getAccessToken());
        apiToken.setExpiresIn(token.getExpiresIn());
        apiToken.setTokenType(token.getTokenType());
        apiToken.setRefreshToken(token.getRefreshToken());
        apiToken.setScope(token.getScope());
        return apiToken;
    }
    
    /**
     * Constructs a {@link HttpRequest} that Stormpath needs in order to provide an access token from a given API key
     * and secret. The Stormpath API also works if you directly give it a <code>HttpServletRequst</code> object but
     * since that is not accessible from this layer we have effectively just rebuild it here from the input parameters).
     * 
     * @param accessKey
     *            Access/API key that has been obtained by the client a priori.
     * @param secret
     *            The secret associated with the API key.
     * @return A {@link HttpRequest}.
     */
    HttpRequest buildHttpRequest(String accessKey, String secret) {
        Map<String, String[]> headers = new HashMap<String, String[]>();

        // These two HTTP headers are mandatory (for Stormpath)
        // Authorization header
        String encodedSecretAndKey = toAuthorizationHttpHeaderFormat(accessKey, secret);
        String[] authHeaderValue = { encodedSecretAndKey };
        headers.put("Authorization", authHeaderValue);

        // Content-Type header
        String[] contentTypeHeaderValue = { "application/x-www-form-urlencoded" };
        headers.put("Content-Type", contentTypeHeaderValue);

        // Mandatory parameters that can come from the request body (usually)
        // TODO for now, it's not necessary to support different scopes or grant types
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        String[] grantTypeValue = { "client_credentials" };
        parameters.put("grant_type", grantTypeValue);

        String[] scopeArray = { null };
        parameters.put("scope", scopeArray);

        return HttpRequests.method(HttpMethod.POST).headers(headers).parameters(parameters).build();
    }

    /**
     * Encodes the access key and secret to the format:
     * 
     * <pre>
     * Basic RjJTS0haQVI0SVVDVUNIQkw1M0xLR0FZWDpOYm10enNaZkpIWkxYRjhzT0ZGckNDRnNxNXpCYW1xaC9GSFNtWVpJcUlN
     * </pre>
     * 
     * "Basic" is the prefix required for Basic Authorization and the long string is the base64 encoded String:
     * <code>key:secret</code>.
     * 
     * @param accessKey
     *            The API key
     * @param secret
     *            The secret.
     * @return HTTP Basic authentication header format.
     */
    String toAuthorizationHttpHeaderFormat(String accessKey, String secret) {
        byte[] unencodedkeyAndSecret = accessKey.concat(":").concat(secret).getBytes();
        String base64EncodedKeyAndSecret = DatatypeConverter.printBase64Binary(unencodedkeyAndSecret);
        return "Basic ".concat(base64EncodedKeyAndSecret);
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
