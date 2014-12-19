package integration.service.auth.stormpath;

import integration.api.model.InsertResult;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.reg.NewUserRegistrationRequest;
import integration.service.auth.RegistrationService;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.directory.CustomData;

@Service
public class StormpathRegistrationService extends AbstractStormpathService implements RegistrationService {

    @Inject
    StormpathRegistrationService(StormpathClientFactory factory, @Value("${application.name}") String applicationName) {
        super(factory, applicationName);
    }

    @Override
    public InsertResult<AccountDao> register(NewUserRegistrationRequest request) {
        // Create the account object
        Account account = getClient().instantiate(Account.class);

        // Set the account properties
        account.setUsername(request.getUsername()); // optional, defaults to email if unset
        account.setEmail(request.getEmail());
        account.setPassword(request.getPassword());
        account.setGivenName("None of your business Stormpath");
        account.setSurname("None of your business Stormpath");
        CustomData customData = account.getCustomData(); // TODO use own repository instead for custom features
        customData.put("alias", request.getAlias());

        // Create the account using the existing Application object
        account = getApplication().createAccount(account);

        AccountDao accountDao = toAccountDao(account);
        InsertResult<AccountDao> result = new InsertResult<AccountDao>(accountDao);
        return result;
    }

    AccountDao toAccountDao(Account account) {
        CustomData customData = account.getCustomData();
        String alias = (String) customData.get("alias");
        AccountDao accountDao = new AccountDao(account.getUsername(), alias);
        accountDao.setEmail(account.getEmail());
        accountDao.setId(account.getHref());
        return accountDao;
    }

}
