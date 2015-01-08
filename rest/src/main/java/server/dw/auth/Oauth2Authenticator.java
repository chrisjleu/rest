package server.dw.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.User;

import com.google.common.base.Optional;

import core.service.UserService;

/**
 * An {@link Authenticator} that returns a {@link User} principal.
 * 
 * @see OAuth2Factory
 */
public class Oauth2Authenticator implements Authenticator<AuthenticationRequest, User> {

    Logger logger = LoggerFactory.getLogger(Oauth2Authenticator.class);

    private final UserService userService;

    public Oauth2Authenticator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<User> authenticate(AuthenticationRequest request) throws AuthenticationException {
        logger.debug("Authorizing request {}", request);

        core.model.User user = userService.authenticate(buildAuthenticationRequest(request));

        if (user == null) {
            logger.debug("Request {} not authorized", request);
            return Optional.absent();
        }

        logger.debug("Authorized request {} for user {}", request, user);

        return Optional.of(new User(user.getEmail(), user.getAlias()));
    }

    core.model.request.AuthenticationRequest buildAuthenticationRequest(AuthenticationRequest request) {
        return new core.model.request.AuthenticationRequest(request.getHttpRequestMethod(),
                request.getHttpRequestHeaders(), request.getHttpQueryString());
    }
}
