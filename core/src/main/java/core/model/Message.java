package core.model;

import java.util.UUID;

import lombok.Data;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This message is part of the core of the application. It is not exposed outside the core module.
 */
@Data
public class Message {

	@JsonProperty("_id")
	private final String id;

	@NotBlank
    @JsonProperty("value")
	private final String value;

    @JsonCreator
    public Message(@JsonProperty("value") String value) {
		this.id = UUID.randomUUID().toString();
		this.value = value;
    }
}
