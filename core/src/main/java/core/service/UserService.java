package core.service;

import integration.api.model.Error;
import integration.api.model.InsertResult;
import integration.api.model.apikey.ApiKey;
import integration.api.model.apikey.ApiToken;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.api.model.user.auth.OauthTokenResponse;
import integration.api.model.user.reg.NewUserRegistrationRequest;
import integration.service.auth.ApiKeyManagementService;
import integration.service.auth.AuthenticationService;
import integration.service.auth.RegistrationService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import core.model.AccessKey;
import core.model.Token;
import core.model.User;
import core.model.request.AuthenticationRequest;
import core.model.response.ApiTokenResponse;

/**
 * Handles all operations related to users.
 */
@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final AuthenticationService authService;

    private final RegistrationService registrationService;

    private final ApiKeyManagementService apiKeyManagementService;

    @Inject
    public UserService(AuthenticationService authenticationService, RegistrationService registrationService,
            ApiKeyManagementService apiKeyManagementService) {
        this.authService = authenticationService;
        this.registrationService = registrationService;
        this.apiKeyManagementService = apiKeyManagementService;
    }

    /**
     * In some authentication mechanisms, an "Access key" or "API key" can be issued to developers that want to use an
     * API that requires authentication. Rather than use their own username and password, they essentially request the
     * API owners to generate a username and password (called a key and secret just to be different) for them and use
     * this instead.
     * 
     * More reasons to use API keys: https://stormpath.com/blog/top-six-reasons-use-api-keys-and-how/
     * 
     * This method should only be used to generate keys for an already authenticated user (i.e. a developer that has
     * logged in to the site already through some other authentication mechanism).
     * 
     * @param username
     *            The username of the user to generate an access key for.
     * @return
     */
    public AccessKey createAccessKey(String username) {
        ApiKey apiKey = apiKeyManagementService.create(username);
        AccessKey.Status accessKeyStatus = AccessKey.Status.DISABLED;
        if (apiKey.getStatus() == ApiKey.Status.ENABLED) {
            accessKeyStatus = AccessKey.Status.ENABLED;
        }
        return new AccessKey(apiKey.getId(), apiKey.getSecret(), accessKeyStatus);
    }

    /**
     * This is an authentication that gives back a token that is intended to be used in subsequent requests to an API.
     * 
     * @param request
     * @return
     */
    public ApiTokenResponse requestToken(String accessKey, String secret) {
        OauthTokenResponse response = authService.authenticateForToken(accessKey, secret);

        Token token = new Token();
        token.setAccessToken(response.getApiToken().getAccessToken());
        token.setExpiresIn(response.getApiToken().getExpiresIn());
        token.setTokenType(response.getApiToken().getTokenType());

        User user = new User(response.getAccount().getId(), response.getAccount().getEmail(), response.getAccount()
                .getAlias());
        
        ApiTokenResponse apiTokenResponse = new ApiTokenResponse();
        apiTokenResponse.setToken(token);
        apiTokenResponse.setUser(user);
        
        return apiTokenResponse;
    }

    public User authenticate(AuthenticationRequest request) {

        AuthenticationResponse result = authService.authenticate(buildAuthenticationRequest(request));
        AccountDao accountDao = result.getAccount();
        if (accountDao == null) {
            // Not authenticated
            logger.debug("\"{}\" not authenticated", request);
            return null;
        } else {
            return new User(accountDao.getId(), accountDao.getEmail(), accountDao.getAlias());
        }
    }

    integration.api.model.apikey.AuthenticationRequest buildAuthenticationRequest(AuthenticationRequest request) {
        return new integration.api.model.apikey.AuthenticationRequest(request.getHttpRequestMethod(),
                request.getHttpRequestHeaders(), request.getHttpQueryString());
    }

    /**
     * <p>
     * Authenticates a user given a username and password.
     * </p>
     * 
     * @param username
     * @param password
     * @return The User that was authenticated or null otherwise.
     */
    public User authenticate(String username, String password) {
        AuthenticationResponse result = authService.authenticate(username, password);
        AccountDao accountDao = result.getAccount();
        if (accountDao == null) {
            // Not authenticated
            logger.debug("Username \"{}\" not authenticated", username);
            return null;
        } else {
            return new User(accountDao.getId(), accountDao.getEmail(), accountDao.getAlias());
        }
    }

    /**
     * Create a new user.
     * 
     * @param email
     * @param alias
     * @param password
     * @return The newly created user.
     */
    public User create(String email, String alias, String password) {
        logger.debug("Proceeding to create user \"{}\"", alias);

        NewUserRegistrationRequest request = new NewUserRegistrationRequest(email, password);
        request.setAlias(alias);
        request.setEmail(email);

        InsertResult<AccountDao> result = registrationService.register(request);
        AccountDao account = result.getInserted();

        if (account == null) {
            Error error = result.getError();
            throw new RuntimeException(error.getMessage() + ", code=" + error.getCode());
        } else {
            User user = new User(account.getId(), email, alias);
            logger.debug("Created user {}", user);
            return user;
        }
    }
}
