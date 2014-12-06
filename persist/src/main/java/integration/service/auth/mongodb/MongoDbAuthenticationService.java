package integration.service.auth.mongodb;

import integration.api.model.PropertyValuePair;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.api.repository.Repository;
import integration.service.auth.AuthenticationService;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("mongo")
public class MongoDbAuthenticationService implements AuthenticationService {

    Logger logger = LoggerFactory.getLogger(MongoDbAuthenticationService.class);

    Repository<AccountDao> userAccountRepository;

    @Inject
    public MongoDbAuthenticationService(Repository<AccountDao> repository) {
        this.userAccountRepository = repository;
    }

    @Override
    public AuthenticationResponse authenticate(String username, String password) {
        logger.debug("Proceeding to authenticate user \"{}\"", username);
        try {
            PropertyValuePair usernamePair = PropertyValuePair.of("username", username);
            AccountDao account = userAccountRepository.find(usernamePair);

            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            if (account == null) {
                logger.debug("Not authenticated: User \"{}\" does not exist", username);
            } else {
                boolean passwordCorrect = PasswordHasher.check(password, account.getPassword());
                if (passwordCorrect) {
                    authenticationResponse.setAccount(account);
                } else {
                    logger.debug("Not authenticated: User \"{}\" password not matched", username);
                }
            }

            return authenticationResponse;
        } catch (Exception e) {
            throw new RuntimeException("Exception while attempting to authenticate user " + username, e);
        }
    }

}
