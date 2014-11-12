package integration.service.auth.service;

import java.util.UUID;

import integration.service.auth.model.Account;
import integration.service.auth.model.AuthenticationResult;

import org.springframework.stereotype.Service;

@Service
public class DummyAuthenticationService implements AuthenticationService {

    public AuthenticationResult authenticate(String username, String password) {
        AuthenticationResult authenticationResult = new AuthenticationResult();
        if ("name".equals(username) && "pwd".equals(password)) {
            Account account = new Account(UUID.randomUUID().toString());
            account.setUsername(username);
            account.setAlias("your-alias");
            account.setEmail(username.concat("@email.com"));
            authenticationResult.setAccount(account);
        }
        return authenticationResult;
    }
}
