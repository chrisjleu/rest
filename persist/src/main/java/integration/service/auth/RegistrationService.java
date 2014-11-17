package integration.service.auth;

import integration.api.model.InsertResult;
import integration.api.model.user.auth.AccountDao;
import integration.api.model.user.reg.NewUserRegistrationRequest;
import integration.api.model.user.reg.NewUserRegistrationResult;

/**
 * Handles exclusively the registration of new users. Implementations could, for instance, register users in a database
 * or with an externally managed solution.
 */
public interface RegistrationService {

    /**
     * Registers a new user.
     * 
     * TODO: Might abstract from {@link AccountDao} using {@link NewUserRegistrationResult} instead if it's useful.
     * 
     * @param request
     *            {@link NewUserRegistrationRequest} encapsulates what is needed to register a new user.
     * @return The result of the request to register a new user.
     */
    public InsertResult<AccountDao> register(NewUserRegistrationRequest request);
}
