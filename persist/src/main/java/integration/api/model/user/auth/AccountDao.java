package integration.api.model.user.auth;

import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@ToString(exclude="password")
public class AccountDao {

    @Id
    String id;

    @NotBlank
    final String username;

    @NotBlank
    final String password;

    String alias;

    String email;
    
    @JsonCreator
    public AccountDao(@JsonProperty("username") String username, @JsonProperty("password") String password) {
        this.username = username;
        this.password = password;
    }
}
