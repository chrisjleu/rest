package api.representations;

import lombok.Data;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import api.validation.Password;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class UserRegistrationRequest {

    @Email
    private final String email;

    @Password
    private final String password;

    @NotBlank
    private final String alias;

    @JsonCreator
    public UserRegistrationRequest(@JsonProperty("email") String email, @JsonProperty("password") String password,
            @JsonProperty("alias") String alias) {
        this.email = email;
        this.password = password;
        this.alias = alias;
    }
}
