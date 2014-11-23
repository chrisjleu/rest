package server.dw;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import server.dw.config.AdminConfig;
import server.dw.config.AuthenticationCachePolicy;
import server.dw.config.FilterChainFactory;
import server.dw.config.SpringContextFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>
 * An object representation of the DropWizard YAML configuration file. Any additions here can be parsed from the
 * <code>yaml</code> file as well.
 * </p>
 */
public class RestApplicationConfiguration extends Configuration {

    // ************************************************** //
    // ************ Admin page configuration ************ //
    // ************************************************** //
    @Valid
    @NotNull
    private AdminConfig adminConfig = new AdminConfig();

    /**
     * Access the configuration for the administration pages with this method.
     * 
     * @return {@link AdminConfig}
     */
    AdminConfig admin() {
        return getAdminConfig();
    }

    @JsonProperty("admin")
    public AdminConfig getAdminConfig() {
        return adminConfig;
    }

    @JsonProperty("admin")
    public void setAdminConfig(AdminConfig adminConfig) {
        this.adminConfig = adminConfig;
    }

    // **************************************** //
    // ************ Custom filters ************ //
    // **************************************** //
    @Valid
    @NotNull
    private FilterChainFactory filterChainFactory = new FilterChainFactory();

    @JsonProperty("filters")
    public FilterChainFactory getFilterChainFactory() {
        return filterChainFactory;
    }

    @JsonProperty("filters")
    public void setFilterChainFactory(FilterChainFactory filterChainFactory) {
        this.filterChainFactory = filterChainFactory;
    }

    // *********************************************************** //
    // ************ Basic authentication cache policy ************ //
    // *********************************************************** //
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

    // **************************************** //
    // ************ Spring context ************ //
    // **************************************** //
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
