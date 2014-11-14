package api.representations;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This {@link Message} object forms part of the API that will be represented in some way to the user (most likely in
 * JSON format).
 */
@ToString
public class Message {

    @Getter
    @NotBlank
    @JsonProperty("v")
    private final String value;

    @Getter
    @JsonProperty("ln")
    private final double longitude;

    @Getter
    @JsonProperty("lt")
    private final double latidude;

    @Getter
    @Setter
    @JsonProperty("cd")
    @JsonIgnore
    private Date creationDate;

    @JsonCreator
    public Message(@JsonProperty("v") String value, @JsonProperty("ln") double lon, @JsonProperty("lt") double lat) {
        this.value = value;
        this.longitude = lon;
        this.latidude = lat;
    }

    // TODO not sure if the two methods below should be in this class...

    public static Message fromModel(core.model.Message m) {
        Message message = new Message(m.getValue(), m.getLatidude(), m.getLongitude());
        message.setCreationDate(m.getCreationDate());
        return message;
    }

}
