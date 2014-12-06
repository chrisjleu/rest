package integration.service.auth.stormpath;

import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.auth.AuthenticationResponse;
import integration.service.auth.AuthenticationService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.authc.AuthenticationRequest;
import com.stormpath.sdk.authc.AuthenticationResult;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.resource.ResourceException;

@Service
public class StormpathAuthenticationService implements AuthenticationService {

    private final StormpathClientFactory clientFactory;

    private final String applicationName;

    private Client client;

    @Inject
    public StormpathAuthenticationService(StormpathClientFactory clientFactory,
            @Value("${application.name}") String applicationName) {
        this.clientFactory = clientFactory;
        this.applicationName = applicationName;
    }

    @PostConstruct
    void init() {
        this.client = clientFactory.instance();
    }

    @Override
    public AuthenticationResponse authenticate(String username, String password) {
        AuthenticationResponse response = new AuthenticationResponse();

        AuthenticationRequest<String, char[]> request = new UsernamePasswordRequest(username, password);
        try {
            AuthenticationResult result = getApplication().authenticateAccount(request);
            Account account = result.getAccount();
            AccountDao accountDao = new AccountDao(account.getUsername(), "alias");
            accountDao.setId(account.getHref());
            accountDao.setEmail(account.getEmail());
            response.setAccount(accountDao);
        } catch (ResourceException ex) {
            System.out.println(ex.getStatus() + " " + ex.getMessage());
        }

        return response;
    }

    private Application getApplication() {
        ApplicationList applications = client.getApplications(Applications.where(Applications.name().eqIgnoreCase(
                applicationName)));

        Application application = applications.iterator().next();
        return application;
    }

}
