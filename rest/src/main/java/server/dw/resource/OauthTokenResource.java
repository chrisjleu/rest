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
 * API token resource. Parts (C) and (D) of <a href="https://tools.ietf.org/html/rfc6750">RFC 6750 The OAuth 2.0
 * Authorization Framework: Bearer Token Usage</a> are implemented here.
 * </p>
 * <p>
 * It is assumed that a client has already made a request to the resource owner (A) and obtained an
 * "authorization grant" (B) in the form of an API key and secret (which is more or less equivalent to a username and
 * password). The next thing the client wants to do is exchange the API key and secret for an access token and then use
 * that token to access protected resources on the "resource" server.
 * 
 * <pre>
 *      +--------+                               +---------------+
 *      |        |--(A)- Authorization Request ->|   Resource    |
 *      |        |                               |     Owner     |
 *      |        |<-(B)-- Authorization Grant ---|               |
 *      |        |                               +---------------+
 *      |        |
 *      |        |                               +---------------+
 *      |        |--(C)-- Authorization Grant -->| Authorization |
 *      | Client |                               |     Server    |
 *      |        |<-(D)----- Access Token -------|               |
 *      |        |                               +---------------+
 *      |        |
 *      |        |                               +---------------+
 *      |        |--(E)----- Access Token ------>|    Resource   |
 *      |        |                               |     Server    |
 *      |        |<-(F)--- Protected Resource ---|               |
 *      +--------+                               +---------------+
 * </pre>
 * 
 * </p>
 */
@Path("/oauth")
@Produces({ MediaType.APPLICATION_JSON })
@Consumes(value = MediaType.APPLICATION_FORM_URLENCODED)
public class OauthTokenResource {

    Logger logger = LoggerFactory.getLogger(OauthTokenResource.class);

    /**
     * <p>
     * This is the "token endpoint" where a client can exchange an API key and secret (much like a username and
     * password) for an access token. The access token is used to make subsequent requests to the "resource server".
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
