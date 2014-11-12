package server.dw;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import server.dw.config.AuthenticationCachePolicy;

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
}
