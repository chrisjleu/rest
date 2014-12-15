package integration.api.model.apikey;

import lombok.Data;

@Data
public class ApiKey {

    private final String id;

    private final String secret;

    private Status status;

    public enum Status {
        ENABLED, DISABLED
    }

    public ApiKey(String id, String secret, Status status) {
        this.id = id;
        this.secret = secret;
        this.status = status;
    }
}
