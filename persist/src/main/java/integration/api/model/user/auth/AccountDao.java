package integration.api.model.user.auth;

import javax.persistence.Id;

import lombok.Data;
import lombok.ToString;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@ToString(exclude="password")
@MongoCollection(name="accounts")
public class AccountDao {

    @Id
    String id;

    @NotBlank
    @Email
    final String username;

    @NotBlank
    final String alias;

    @NotBlank
    String password;

    @NotBlank
    @Email
    String email;
    
    @JsonCreator
    public AccountDao(@JsonProperty("username") String username, @JsonProperty("alias") String alias) {
        this.username = username;
        this.alias = alias;
    }
}
