package integration.service.auth;

import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResult;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Here before a real implementation is decided upon.
 */
@Service("dummyAuthService")
@Profile("standalone")
public class DummyAuthenticationService implements AuthenticationService {

    public AuthenticationResult authenticate(String username, String password) {

        AuthenticationResult authenticationResult = new AuthenticationResult();
        if ("name".equals(username) && "pwd".equals(password)) {
            AccountDao accountDao = new AccountDao(username, password);
            accountDao.setId(UUID.randomUUID().toString());
            accountDao.setAlias("your-alias");
            accountDao.setEmail(username.concat("@email.com"));
            authenticationResult.setAccount(accountDao);
        }

        return authenticationResult;
    }
}
