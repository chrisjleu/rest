package server.dw.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.User;

import com.google.common.base.Optional;

import core.service.UserService;

public class UserBasicAuthAuthenticator implements Authenticator<BasicCredentials, User> {

    Logger logger = LoggerFactory.getLogger(UserBasicAuthAuthenticator.class);

    private final UserService userService;

    public UserBasicAuthAuthenticator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials creds) throws AuthenticationException {
        logger.debug("Authenticating \"{}\"", creds);
        
        core.model.User authenticatedUser = userService.authenticate(creds.getUsername(), creds.getPassword());
        if (authenticatedUser == null) {
            return Optional.absent();
        }

        return Optional.of(new User(authenticatedUser.getEmail(), authenticatedUser.getAlias()));
    }

}
