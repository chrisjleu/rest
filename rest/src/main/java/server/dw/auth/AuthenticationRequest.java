package server.dw.auth;

import java.util.Map;

import lombok.Data;

@Data
public class AuthenticationRequest {

    private final String httpRequestMethod;

    private final Map<String, String[]> httpRequestHeaders;

    private final String httpQueryString;

    public AuthenticationRequest(String httpRequestMethod, Map<String, String[]> httpRequestHeaders,
            String httpQueryString) {
        this.httpRequestMethod = httpRequestMethod;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpQueryString = httpQueryString;
    }

}
