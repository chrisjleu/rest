package integration.api.model.user.reg;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Encapsulates what is required to register a new user.
 */
@Data
public class NewUserRegistrationRequest {

    @NotNull
    final String username;

    @NotNull
    final String password;

    String alias;
    
    String email;
    
    public NewUserRegistrationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
