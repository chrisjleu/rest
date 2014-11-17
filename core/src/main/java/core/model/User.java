package core.model;

import lombok.Data;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

/**
 * A User POJO.
 */
@Data
public class User {

    @NotBlank
    private final String id;

    @NotBlank
    private final String alias;

    @Email
    private final String email;

    public User(String id, String email, String alias) {
        this.id = id;
        this.email = email;
        this.alias = alias;
    }
}
