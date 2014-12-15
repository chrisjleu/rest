package core.service;

import integration.api.model.Error;
import integration.api.model.InsertResult;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.api.model.user.reg.NewUserRegistrationRequest;
import integration.service.auth.AuthenticationService;
import integration.service.auth.RegistrationService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import core.model.User;
import core.model.request.AuthenticationRequest;

/**
 * Handles all operations related to users.
 */
@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final AuthenticationService authService;

    private final RegistrationService registrationService;

    @Inject
    public UserService(AuthenticationService authenticationService, RegistrationService registrationService) {
        this.authService = authenticationService;
        this.registrationService = registrationService;
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
