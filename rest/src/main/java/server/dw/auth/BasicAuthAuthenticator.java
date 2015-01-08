package server.dw.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.TokenResponse;
import api.representations.User;

import com.google.common.base.Optional;

import core.model.Token;
import core.model.response.ApiTokenResponse;
import core.service.UserService;

/**
 * Basic authentication is only supported in the case that a developer wants to exchange and API Key + secret for an
 * Oauth2 Bearer token. All other authentication requests are validated with Oauth2 bearer tokens.
 */
public class BasicAuthAuthenticator implements Authenticator<BasicCredentials, User> {

    Logger logger = LoggerFactory.getLogger(BasicAuthAuthenticator.class);

    private final UserService userService;

    public BasicAuthAuthenticator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials creds) throws AuthenticationException {
        logger.debug("\"{}\" is requesting an access token", creds.getUsername());

        // TODO the api-key and secret are not the only things that are required - we need the grant type and scope and
        // other headers should be checked... This would require a different factory from the one provided by DW
        ApiTokenResponse apiTokenResponse = userService.requestToken(creds.getUsername(), creds.getPassword());
        User user = buildUser(apiTokenResponse);
        return Optional.of(user);
    }

    User buildUser(ApiTokenResponse apiTokenResponse) {
        User user = new User(apiTokenResponse.getUser().getEmail(), apiTokenResponse.getUser().getAlias());
        TokenResponse token = buildTokenResponse(apiTokenResponse.getToken());
        user.setToken(token);
        return user;
    }

    TokenResponse buildTokenResponse(Token token) {
        if (token != null) {
            return new TokenResponse(token.getAccessToken(), token.getTokenType(), token.getExpiresIn(),
                    token.getRefreshToken(), token.getScope());
        }

        return null;
    }

}
