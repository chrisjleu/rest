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
    private String username;

    @Email
    private String email;

    @JsonCreator
    public User(@JsonProperty("username") String username, @JsonProperty("email") String email) {
        this.username = username;
        this.email = email;
    }
}
