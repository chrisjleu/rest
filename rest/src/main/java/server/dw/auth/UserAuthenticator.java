package server.dw.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.User;

import com.google.common.base.Optional;

import core.service.UserService;

public class UserAuthenticator implements Authenticator<BasicCredentials, User> {

    Logger logger = LoggerFactory.getLogger(UserAuthenticator.class);

    private final UserService userService;

    public UserAuthenticator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials creds) throws AuthenticationException {
        core.model.User authenticatedUser = userService.authenticate(creds.getUsername(), creds.getPassword());
        if (authenticatedUser == null) {
            return Optional.absent();
        }
        return Optional.of(new User(authenticatedUser.getAlias()));
    }

}
