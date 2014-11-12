package core.model;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class User {

    @Id
    @Setter(AccessLevel.NONE)
    private String id;

    @NotNull
    @Setter(AccessLevel.NONE)
    private String username;

    @NotNull
    @Setter(AccessLevel.NONE)
    private String alias;

    @Email
    @Setter(AccessLevel.NONE)
    private String email;

    @JsonCreator
    public User(@JsonProperty("_id") String id, @JsonProperty("username") String username,
            @JsonProperty("email") String email, @JsonProperty("alias") String alias) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.alias = alias;
    }
}
