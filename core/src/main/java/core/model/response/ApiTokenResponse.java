package core.model.response;

import lombok.Data;
import core.model.Token;
import core.model.User;

@Data
public class ApiTokenResponse {

    private User user;
    
    private Token token;
}
