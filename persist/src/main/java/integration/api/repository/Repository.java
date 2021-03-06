package integration.api.repository;

import integration.api.model.InsertResult;
import integration.api.model.PropertyValuePair;

import java.util.List;

/**
 * An abstraction of a data store (repository).
 */
public interface Repository<T> {

    /**
     * Returns that class that this repository is operating on.
     * 
     * @return
     */
    public Class<T> getType();

    /**
     * Find an item in the repository by matching on the given property-value pairs. All values must be matched in order
     * for the object to be found.
     * 
     * @param propertyValues
     * @return
     */
    public T find(PropertyValuePair... propertyValues);

    /**
     * Persists the object in the repository.
     * 
     * @param insertable
     *            The object to persist.
     * @return
     */
    public InsertResult<T> insert(T insertable);

    /**
     * Clears out the entire repository of all data.
     */
    public void drop();

    /**
     * Returns the total number of <code>T</code> objects in the repository.
     * 
     * @return
     */
    public long count();

    /**
     * Returns a list of all <code>T</code> objects that exist in the repository, up to a sensible maximum number.
     * 
     * @return
     */
    public List<T> all();

    /**
     * Returns all all <code>T</code> objects from a point within a radius.
     * 
     * @return
     */
    public List<T> allInRange(double ln, double lt, double maxDistance);
}
