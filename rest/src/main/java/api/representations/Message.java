package api.representations;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This {@link Message} object forms part of the API that will be represented in some way to the user (most likely in
 * JSON format).
 */
public class Message {

    @NotBlank
    @JsonProperty("v")
    private final String value;

    @JsonCreator
    public Message(@JsonProperty("v") String value) {
        this.value = value;
    }

    // TODO not sure if the two methods below should be in this class...

    public static Message fromModel(core.model.Message m) {
        return new Message(m.getValue());
    }

    public static core.model.Message toModel(Message m) {
        return new core.model.Message(m.getValue());
    }

    // //////////////////// GETTERS AND SETTERS //////////////////////////

    public String getValue() {
        return value;
    }

}
