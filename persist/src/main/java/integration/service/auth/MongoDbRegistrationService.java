package integration.service.auth;

import integration.api.model.InsertResult;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.reg.NewUserRegistrationRequest;
import integration.api.repository.Repository;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * This {@link RegistrationService} creates users in a MongoDb repository.
 */
@Service
public class MongoDbRegistrationService implements RegistrationService {

    Logger logger = LoggerFactory.getLogger(MongoDbRegistrationService.class);

    Repository<AccountDao> userAccountRepository;

    @Inject
    public MongoDbRegistrationService(Repository<AccountDao> repository) {
        this.userAccountRepository = repository;
    }

    @Override
    public InsertResult<AccountDao> register(NewUserRegistrationRequest request) {
        String passwordHash;
        try {
            passwordHash = PasswordHasher.hashPassword(request.getPassword());

            AccountDao account = new AccountDao(request.getUsername(), passwordHash);
            account.setAlias(request.getAlias());
            account.setEmail(request.getEmail());

            logger.debug("Proceeding to create account {}", account);

            return userAccountRepository.insert(account);
        } catch (Exception e) {
            throw new RuntimeException("Unable to register user " + request.getUsername(), e);
        }
    }

}
