package integration.api.model.message;

import javax.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotBlank;
import org.mongojack.MongoCollection;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A repository object that represents a message.
 */
@EqualsAndHashCode
@ToString
@MongoCollection(name="messages")
public class MessageDao {

    @Id
    private String id;

    @NotBlank
    private final String value;

    private final double longitude;

    private final double latidude;

    @JsonCreator
    public MessageDao(@JsonProperty("val") String value, @JsonProperty("lt") double lat, @JsonProperty("ln") double lon) {
        this.value = value;
        this.latidude = lat;
        this.longitude = lon;
    }

    public String getId() {
        return id;
    }

    @JsonProperty("val")
    public String getValue() {
        return value;
    }

    @JsonProperty("ln")
    public double getLongitude() {
        return longitude;
    }

    @JsonProperty("lt")
    public double getLatidude() {
        return latidude;
    }

    public void setId(String id) {
        this.id = id;
    }
}
