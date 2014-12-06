package integration.service.auth.dummy;

import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.service.auth.AuthenticationService;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Here before a real implementation is decided upon.
 */
@Service("dummyAuthService")
@Profile("standalone")
public class DummyAuthenticationService implements AuthenticationService {

    public AuthenticationResponse authenticate(String username, String password) {

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        if ("name".equals(username) && "pwd".equals(password)) {
            AccountDao accountDao = new AccountDao(username, "alias");
            accountDao.setId(UUID.randomUUID().toString());
            accountDao.setEmail(username.concat("@email.com"));
            authenticationResponse.setAccount(accountDao);
        }

        return authenticationResponse;
    }
}
