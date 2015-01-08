package server.dw.auth;

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

/**
 * A Jersey provider for OAuth2 bearer tokens. The {@link Authenticator} provided by this Factory accepts
 * {@link AuthenticationRequest} requests.
 * 
 * @param <T>
 *            the principal type.
 */
public final class OAuth2Factory<T> extends AuthFactory<AuthenticationRequest, T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2Factory.class);
    private static final String PREFIX = "Bearer";
    private static final String CHALLENGE_FORMAT = PREFIX + " realm=\"%s\"";

    private final boolean required;
    private final Class<T> generatedClass;
    private final String realm;

    @Context
    private HttpServletRequest request;

    public OAuth2Factory(final Authenticator<AuthenticationRequest, T> authenticator, final String realm,
            final Class<T> generatedClass) {
        super(authenticator);
        this.required = false;
        this.realm = realm;
        this.generatedClass = generatedClass;
    }

    private OAuth2Factory(final boolean required, final Authenticator<AuthenticationRequest, T> authenticator,
            final String realm, final Class<T> generatedClass) {
        super(authenticator);
        this.required = required;
        this.realm = realm;
        this.generatedClass = generatedClass;
    }

    @Override
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public AuthFactory<AuthenticationRequest, T> clone(boolean required) {
        return new OAuth2Factory<>(required, authenticator(), this.realm, this.generatedClass);
    }

    @Override
    public T provide() {
        try {
            final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header != null) {
                final int space = header.indexOf(' ');
                if (space > 0) {
                    final String method = header.substring(0, space);
                    if (PREFIX.equalsIgnoreCase(method)) {
                        final String credentials = header.substring(space + 1);
                        final Optional<T> result = authenticator().authenticate(convertRequest(request));
                        if (result.isPresent()) {
                            return result.get();
                        }
                    }
                }
            }
        } catch (AuthenticationException e) {
            LOGGER.warn("Error authenticating credentials", e);
            throw new InternalServerErrorException();
        }

        if (required) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .header(HttpHeaders.WWW_AUTHENTICATE, String.format(CHALLENGE_FORMAT, realm))
                    .type(MediaType.TEXT_PLAIN_TYPE).entity("Credentials are required to access this resource.")
                    .build());
        }

        return null;
    }

    @Override
    public Class<T> getGeneratedClass() {
        return generatedClass;
    }

    AuthenticationRequest convertRequest(HttpServletRequest request) {
        Map<String, String[]> queryParams = request.getParameterMap(); // TODO Need to create space for this

        return new AuthenticationRequest(request.getMethod(), convertHeaders(request), request.getQueryString());
    }

    Map<String, String[]> convertHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String[]> headerMap = new HashMap<String, String[]>();
        while (headerNames.hasMoreElements()) {
            String headerName = (String) headerNames.nextElement();
            List<String> headerValueList = Collections.list(request.getHeaders(headerName));
            headerMap.put(headerName, headerValueList.toArray(new String[headerValueList.size()]));
        }

        return headerMap;
    }
}
