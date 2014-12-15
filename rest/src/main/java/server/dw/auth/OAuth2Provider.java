package server.dw.auth;

import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.HttpRequestContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

/**
 * An Oauth2 provider that works with a <code>Authenticator<HttpRequestContext, T></code>.
 * 
 * @param <T>
 */
public class OAuth2Provider<T> implements InjectableProvider<Auth, Parameter> {

    private final Authenticator<AuthenticationRequest, T> authenticator;

    private final String realm;

    /**
     * Creates a new OAuthProvider with the given {@link Authenticator} and realm.
     * 
     * @param authenticator
     *            the authenticator which will take the OAuth2 bearer token and convert them into instances of {@code T}
     * @param realm
     *            the name of the authentication realm
     */
    public OAuth2Provider(Authenticator<AuthenticationRequest, T> authenticator, String realm) {
        this.authenticator = authenticator;
        this.realm = realm;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<?> getInjectable(ComponentContext ic, Auth a, Parameter c) {
        return new OAuth2Injectable<>(authenticator, realm, a.required());
    }

    private static class OAuth2Injectable<T> extends AbstractHttpContextInjectable<T> {
        private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Injectable.class);
        private static final String CHALLENGE_FORMAT = "Bearer realm=\"%s\"";
        private static final String BEARER_SCHEME = "Bearer";

        private final Authenticator<AuthenticationRequest, T> authenticator;
        private final String realm;
        private final boolean required;

        private OAuth2Injectable(Authenticator<AuthenticationRequest, T> authenticator, String realm, boolean required) {
            this.authenticator = authenticator;
            this.realm = realm;
            this.required = required;
        }

        @Override
        public T getValue(HttpContext context) {
            try {
                HttpRequestContext request = context.getRequest();
                final String header = request.getHeaderValue(HttpHeaders.AUTHORIZATION);
                if (header != null) {
                    final int space = header.indexOf(' ');
                    if (space > 0) {
                        final String authenticationScheme = header.substring(0, space);
                        if (BEARER_SCHEME.equalsIgnoreCase(authenticationScheme)) {
                            final Optional<T> result = authenticator.authenticate(convertRequest(request));
                            if (result.isPresent()) {
                                return result.get();
                            }
                        }
                    }
                }
            } catch (AuthenticationException e) {
                LOGGER.warn("Error authenticating credentials", e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }

            if (required) {
                final String challenge = String.format(CHALLENGE_FORMAT, realm);
                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, challenge)
                        .entity("Credentials are required to access this resource.")
                        .type(MediaType.APPLICATION_JSON_TYPE)
                        .build());
            }

            return null;
        }

        AuthenticationRequest convertRequest(HttpRequestContext request) {
            MultivaluedMap<String, String> headers = request.getRequestHeaders();
            MultivaluedMap<String, String> queryParams = request.getQueryParameters(); // TODO

            return new AuthenticationRequest(request.getMethod(), convertHeaders(headers), toString(queryParams));
        }

        Map<String, String[]> convertHeaders(MultivaluedMap<String, String> headers) {
            Map<String, String[]> headerMap = new HashMap<String, String[]>(headers.size());
            for (Entry<String, List<String>> entry : headers.entrySet()) {
                headerMap.put(entry.getKey(), entry.getValue().toArray(new String[headers.size()]));
            }

            return headerMap;
        }

        String toString(MultivaluedMap<String, String> queryParams) {
            return queryParams.toString(); // TODO implement properly
        }
    }
}
