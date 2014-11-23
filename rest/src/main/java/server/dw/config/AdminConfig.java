package server.dw.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminConfig {

    // **************************************** //
    // ************ Login security ************ //
    // **************************************** //
    @Valid
    @NotNull
    private AdminSecurityProvider adminSecurityProvider = new AdminSecurityProvider();

    @JsonProperty("security")
    public AdminSecurityProvider getAdminSecurityProvider() {
        return adminSecurityProvider;
    }

    @JsonProperty("security")
    public void setAdminSecurityProvider(AdminSecurityProvider adminSecurityProvider) {
        this.adminSecurityProvider = adminSecurityProvider;
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

}
