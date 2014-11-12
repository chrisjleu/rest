package integration.service.auth.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

@Data
public class Account {

    String id;

    String username;

    String alias;

    String email;

    @Getter(AccessLevel.NONE)
    String password;
    
    public Account(String accountId) {
        this.id = accountId;
    }
}
