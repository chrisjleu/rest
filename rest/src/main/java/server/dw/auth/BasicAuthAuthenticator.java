package server.dw.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.TokenResponse;

import com.google.common.base.Optional;

import core.model.Token;
import core.service.UserService;

public class BasicAuthAuthenticator implements Authenticator<BasicCredentials, TokenResponse> {

    Logger logger = LoggerFactory.getLogger(BasicAuthAuthenticator.class);

    private final UserService userService;

    public BasicAuthAuthenticator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<TokenResponse> authenticate(BasicCredentials creds) throws AuthenticationException {
        logger.debug("\"{}\" wants an access token", creds.getUsername());

        // TODO the api-key and secret are not the only things that are required - we need the grant type and scope and
        // other headers should be checked... but how can we get these? Create another provider probably
        Token token = userService.requestToken(creds.getUsername(), creds.getPassword());
        if (token == null) {
            logger.debug("TokenResponse not granted to \"{}\"", creds.getUsername());
            return Optional.absent();
        }

        TokenResponse response = new TokenResponse(token.getAccessToken(), token.getTokenType(), token.getExpiresIn(),
                token.getRefreshToken(), token.getScope());
        logger.debug("\"{}\" granted token \"{}\"", creds.getUsername(), response);
        return Optional.of(response);
    }

}
