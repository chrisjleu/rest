package core.integration;

import javax.validation.constraints.NotNull;

import com.mongodb.MongoClient;

/**
 * <P>
 * Encapsulates information needed to create a {@link MongoClient}.
 * </p>
 */
public class MongoDbConfiguration {

    /**
     * A URI in the format:
     * 
     * <pre>
     * mongodb://host:port
     * </pre>
     */
    private String mongoClientUri;

    /**
     * Optional name of the database.
     */
    private String dbName;

    /**
     * Optional name of the collection to be set.
     */
    private String collName;

    /**
     * The mongo client URI must be supplied but other values are optional.
     * 
     * @param mongoClientUri
     *            A URI in the format: <code>mongodb://host:port</code>
     * @param dbName
     *            Optional name of the database.
     * @param collName
     *            Optional name of the collection to be set.
     */
    public MongoDbConfiguration(@NotNull String mongoClientUri, String dbName, String collName) {
        this.mongoClientUri = mongoClientUri;
        this.dbName = dbName;
        this.collName = collName;
    }

    // ////////////////// GETTERS AND SETTERS //////////////////////

    public String getMongoClientUri() {
        return mongoClientUri;
    }

    public String getDbName() {
        return dbName;
    }

    public String getCollName() {
        return collName;
    }

}