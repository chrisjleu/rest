package integration.service.auth;

import integration.api.model.PropertyValuePair;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResult;
import integration.api.repository.Repository;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MongoDbAuthenticationService implements AuthenticationService {

    Logger logger = LoggerFactory.getLogger(MongoDbAuthenticationService.class);

    Repository<AccountDao> userAccountRepository;

    @Inject
    public MongoDbAuthenticationService(Repository<AccountDao> repository) {
        this.userAccountRepository = repository;
    }

    @Override
    public AuthenticationResult authenticate(String username, String password) {
        logger.debug("Proceeding to authenticate user \"{}\"", username);
        try {
            PropertyValuePair usernamePair = PropertyValuePair.of("username", username);
            AccountDao account = userAccountRepository.find(usernamePair);

            AuthenticationResult authenticationResult = new AuthenticationResult();
            if (account == null) {
                logger.debug("Not authenticated: User \"{}\" does not exist", username);
            } else {
                boolean passwordCorrect = PasswordHasher.check(password, account.getPassword());
                if (passwordCorrect) {
                    authenticationResult.setAccount(account);
                } else {
                    logger.debug("Not authenticated: User \"{}\" password not matched", username);
                }
            }

            return authenticationResult;
        } catch (Exception e) {
            throw new RuntimeException("Exception while attempting to authenticate user " + username, e);
        }
    }

}
