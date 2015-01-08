package integration.api.model.user.auth;

import integration.api.model.apikey.ApiToken;
import lombok.Data;

/**
 * Encapsulates a failed or successful request for an API Oauth2 token.
 */
@Data
public class OauthTokenResponse {

    AccountDao account;
    
    ApiToken apiToken;
}
