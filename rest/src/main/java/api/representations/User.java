package api.representations;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class User {

    @NotNull
    private final String alias;

    @Email
    private final String email;

    @JsonCreator
    public User(@JsonProperty("email") String email, @JsonProperty("alias") String alias) {
        this.email = email;
        this.alias = alias;
    }

    // TODO not sure if the two methods below should be in this class...

    public static User fromModel(core.model.User user) {
        return new User(user.getEmail(), user.getAlias());
    }
}
