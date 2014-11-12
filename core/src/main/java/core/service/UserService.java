package core.service;

import integration.api.repository.Repository;
import integration.service.auth.model.Account;
import integration.service.auth.model.AuthenticationResult;
import integration.service.auth.service.AuthenticationService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import core.model.User;

/**
 * Handles all operations related to users.
 */
@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    private final Repository<User> repository;

    private final AuthenticationService authService;

    @Inject
    public UserService(Repository<User> repository, AuthenticationService authenticationService) {
        this.repository = repository;
        this.authService = authenticationService;
    }

    /**
     * Counts the number of users in the repository.
     * 
     * @return The total number of users in the repository.
     */
    public long count() {
        return repository.count();
    }

    /**
     * <p>
     * Authenticates a user.
     * </p>
     * TODO: This should probably return and AuthenticationResult object or something similar at some point.
     * 
     * @param username
     * @param password
     * @return The User that was authenticated or null otherwise.
     */
    public User authenticate(String username, String password) {
        AuthenticationResult result = authService.authenticate(username, password);
        Account account = result.getAccount();
        if (account == null) {
            // Not authenticated
            return null;
        } else {
            return new User(account.getId(), account.getUsername(), account.getEmail(), account.getAlias());
        }
    }
}
