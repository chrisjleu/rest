package integration.api.model.apikey;

import lombok.Data;

@Data
public class ApiToken {

    private String accessToken;

    private String tokenType;

    private String expiresIn;

    private String refreshToken;

    private String scope;

}
