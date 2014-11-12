package api.representations;

import javax.validation.constraints.NotNull;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class User {

    @NotNull
    private final String alias;

    @JsonCreator
    public User(@JsonProperty("alias") String alias) {
        this.alias = alias;
    }
}
