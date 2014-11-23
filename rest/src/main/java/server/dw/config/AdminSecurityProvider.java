package server.dw.config;

import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import server.dw.auth.AdminConstraintSecurityHandler;

public class AdminSecurityProvider {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    public ConstraintSecurityHandler instance() {
        return new AdminConstraintSecurityHandler(username, password);
    }

    // ////////////// GETTERS AND SETTERS ///////////////////

    @JsonProperty
    public String getUsername() {
        return username;
    }

    @JsonProperty
    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }
}
