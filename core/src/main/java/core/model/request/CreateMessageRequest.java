package core.model.request;

import lombok.Data;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Represents a request to create (and persist) a new message.
 */
@Data
public class CreateMessageRequest {

    @NotBlank
    private final String value;

    private final double longitude;

    private final double latidude;

    public CreateMessageRequest(String value, double lat, double lon) {
        this.value = value;
        this.latidude = lat;
        this.longitude = lon;
    }
}
