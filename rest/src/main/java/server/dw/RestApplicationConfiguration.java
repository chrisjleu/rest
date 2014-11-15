package server.dw;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import server.dw.config.AuthenticationCachePolicy;
import server.dw.config.SpringContextFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * An object representation of the DropWizard YAML configuration file. Any additions here can be parsed from the
 * <code>yaml</code> file as well.
 * </p>
 */
public class RestApplicationConfiguration extends Configuration {

    @Valid
    @NotNull
    AuthenticationCachePolicy authenticationCachePolicy = new AuthenticationCachePolicy();

    @JsonProperty("authenticationCachePolicy")
    public AuthenticationCachePolicy getAuthenticationCachePolicy() {
        return authenticationCachePolicy;
    }

    @JsonProperty("authenticationCachePolicy")
    public void setAuthenticationCachePolicy(AuthenticationCachePolicy authenticationCachePolicy) {
        this.authenticationCachePolicy = authenticationCachePolicy;
    }

    @Valid
    @NotNull
    private SpringContextFactory springContextFactory = new SpringContextFactory();

    @JsonProperty("springContext")
    public SpringContextFactory getSpringContextFactory() {
        return springContextFactory;
    }

    @JsonProperty("springContext")
    public void setSpringContextFactory(SpringContextFactory springContextFactory) {
        this.springContextFactory = springContextFactory;
    }

}
