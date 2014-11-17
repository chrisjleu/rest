package integration.service.auth;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import integration.api.model.PropertyValuePair;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResult;
import integration.api.repository.Repository;

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
        
        PropertyValuePair usernamePair = PropertyValuePair.of("username", username);
        PropertyValuePair passwordPair = PropertyValuePair.of("password", password);
        AccountDao account = userAccountRepository.find(usernamePair, passwordPair);
        
        AuthenticationResult authenticationResult = new AuthenticationResult();
        authenticationResult.setAccount(account);
        return authenticationResult;
    }

}
