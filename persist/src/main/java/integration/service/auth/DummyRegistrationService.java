package integration.service.auth;

import integration.api.model.InsertResult;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.reg.NewUserRegistrationRequest;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A dummy implementation that relies on no external systems.
 */
@Service("dummy")
@Profile("standalone")
public class DummyRegistrationService implements RegistrationService {

    @Override
    public InsertResult<AccountDao> register(NewUserRegistrationRequest request) {
        AccountDao dao = new AccountDao(request.getUsername(), request.getPassword());
        dao.setAlias("Alias");
        dao.setEmail(request.getUsername().concat("@gmail.com"));
        dao.setId(UUID.randomUUID().toString());
        return new InsertResult<AccountDao>(dao);
    }

}
