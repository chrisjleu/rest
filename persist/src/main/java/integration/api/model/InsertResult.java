package integration.api.model;

import lombok.Data;

/**
 * Represents the result of an attempt to persist something to a repository.
 * 
 * @param <T>
 *            The persisted object.
 */
@Data
public class InsertResult<T> {

    private final T inserted;

    private final Error error;

    public InsertResult(Error error) {
        this.error = error;
        this.inserted = null;
    }

    public InsertResult(T inserted) {
        this.inserted = inserted;
        this.error = null;
    }
}
