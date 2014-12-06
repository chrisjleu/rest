package server.dw.resource;

import io.dropwizard.auth.Auth;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.User;
import api.representations.UserRegistrationRequest;

import com.codahale.metrics.annotation.Timed;

import core.service.UserService;

/**
 * <p>
 * Resources map various aspects of incoming HTTP requests to POJOs and perform the reverse process for outgoing HTTP
 * responses. This part is therefore very much the API to the application. The methods here describe what can resources
 * are available. The method parameters describe of course what must be provided in order to obtain the resource and
 * return types are the <a
 * href="https://dropwizard.github.io/dropwizard/manual/core.html#man-core-representations">representations</a> of the
 * resource being requested (in the case of DropWiard, it's likely to be a JSON representation of the object).
 * </p>
 * <p>
 * Resources related to logging in and registering users.
 * </p>
 */
@Path("/user")
@Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN })
public class AuthenticationResource {

    Logger logger = LoggerFactory.getLogger(AuthenticationResource.class);

    private final UserService userService;

    public AuthenticationResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * Send for example:
     * 
     * <pre>
     * curl -X POST -H "Content-Type: application/json" -d '{"alias":"JSmith"}' --user username:password http://localhost:8080/greet
     * </pre>
     * 
     * @param message
     * @return
     */
    @POST
    @Timed
    @Path("/greet")
    public String greet(@Auth User user) {
        logger.debug("Authenticating {}", user);
        return "Congratulations ".concat(user.getAlias()).concat(". You are authenticated.");
    }

    @POST
    @Timed
    @Path("/reg")
    public User register(@Valid UserRegistrationRequest req) {
        core.model.User user = userService.create(req.getEmail(), req.getAlias(), req.getPassword());
        return User.fromModel(user);
    }
}
