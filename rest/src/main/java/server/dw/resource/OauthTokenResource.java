package server.dw.resource;

import io.dropwizard.auth.Auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.TokenResponse;

/**
 * <p>
 * API token resource.
 * </p>
 */
@Path("/oauth")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
public class OauthTokenResource {

    Logger logger = LoggerFactory.getLogger(OauthTokenResource.class);

    /**
     * <p>
     * Exchange an API key and secret (much like a username and password) for an access token that can be used to make
     * subsequent requests to the API.
     * 
     * <pre>
     * curl -L -X POST -H "Content-Type: application/x-www-form-urlencoded" -H "X-Forwarded-Proto: https" -d 'grant_type=client_credentials' -u "8GR88GDIE49UOGFP0R3LQOEB1:pqTa17hVs+rNiIxi/VIxqzHKpKSVbtdcgfy5JOaUDa9" http://localhost:8080/oauth/token
     * </pre>
     * 
     * </p>
     * <p>
     * An example response:
     * 
     * <pre>
     * HTTP 200 OK
     * Content-Type: application/json
     * 
     * {
     *    "access_token":"7FRhtCNRapj9zs.YI8MqPiS8hzx3wJH4.qT29JUOpU64T",
     *    "token_type":"bearer",
     *    "expires_in":3600
     * }
     * </pre>
     * 
     * </p>
     * 
     * @param tokenResponse
     * @return The token and the metadata associated with it.
     */
    @POST
    @Path("/token")
    public TokenResponse exchangeForToken(@Auth TokenResponse tokenResponse) {
        logger.debug("TokenResponse {} granted", tokenResponse);
        return tokenResponse;
    }

}
