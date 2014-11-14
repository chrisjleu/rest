package integration.api.model;

import lombok.Data;

@Data
public class Error {

    public static final int CODE_NOT_SET = 0;

    final private int code;

    final private String message;

    public Error(String message) {
        this.code = CODE_NOT_SET;
        this.message = message;
    }

    public Error(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
