package core.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude="secret")
public class AccessKey {

    private final String id;

    private final String secret;

    private Status status;

    public enum Status {
        ENABLED, DISABLED
    }

    public AccessKey(String id, String secret, Status status) {
        this.id = id;
        this.secret = secret;
        this.status = status;
    }
}
