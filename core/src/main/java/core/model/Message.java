package core.model;

import java.util.Date;

import javax.persistence.Id;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This message is part of the core of the application. It is not exposed outside the core module.
 */
@EqualsAndHashCode
@ToString
public class Message {

    @Id
    private String id;

    @NotBlank
    private final String value;

    private final double longitude;

    private final double latidude;

    private Date creationDate;
    
    @JsonCreator
    public Message(@JsonProperty("val") String value, @JsonProperty("lt") double lat, @JsonProperty("ln") double lon) {
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setId(String id) {
        this.id = id;
    }
}
