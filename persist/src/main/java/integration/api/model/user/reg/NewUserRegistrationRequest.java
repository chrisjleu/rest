package integration.api.model.user.reg;

import lombok.Data;

/**
 * Encapsulates what is required to register a new user.
 */
@Data
public class NewUserRegistrationRequest {

    final String username;

    final String password;

    String alias;
    
    String email;
    
    public NewUserRegistrationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
