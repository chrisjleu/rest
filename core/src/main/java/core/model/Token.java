package core.model;

import lombok.Data;

@Data
public class Token {

    private String accessToken;

    private String tokenType;

    private String expiresIn;

    private String refreshToken;

    private String scope;

    private String applicationHref;

}
