package integration.service.auth.stormpath;

import integration.api.model.apikey.ApiKey;
import integration.service.auth.ApiKeyManagementService;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.account.AccountCriteria;
import com.stormpath.sdk.account.AccountList;
import com.stormpath.sdk.account.Accounts;
import com.stormpath.sdk.api.ApiKeyStatus;

@Service
public class StormpathApiKeyManagementService implements ApiKeyManagementService {

    private StormpathClientHelper stormpathClientHelper;

    @Inject
    public StormpathApiKeyManagementService(StormpathClientHelper stormpathClientHelper) {
        this.stormpathClientHelper = stormpathClientHelper;
    }

    @Override
    public ApiKey create(String username) {
        AccountCriteria criteria = Accounts.where(Accounts.username().eqIgnoreCase(username));
        AccountList accounts = stormpathClientHelper.getApplication().getAccounts(criteria);

        if (accounts == null) {
            return null;
        } else {
            Account account = accounts.iterator().next();
            com.stormpath.sdk.api.ApiKey stormpathApiKey = account.createApiKey();
            ApiKeyStatus status = stormpathApiKey.getStatus();

            ApiKey.Status apiKeyStatus = ApiKey.Status.DISABLED;
            if (status == ApiKeyStatus.ENABLED) {
                apiKeyStatus = ApiKey.Status.ENABLED;
            }
            return new ApiKey(stormpathApiKey.getId(), stormpathApiKey.getSecret(), apiKeyStatus);
        }
    }

    @Override
    public List<ApiKey> list() {
        throw new UnsupportedOperationException("Implementation coming soon!");
    }

    @Override
    public void disable() {
        throw new UnsupportedOperationException("Implementation coming soon!");
    }

    @Override
    public void enable() {
        throw new UnsupportedOperationException("Implementation coming soon!");
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Implementation coming soon!");
    }

}
