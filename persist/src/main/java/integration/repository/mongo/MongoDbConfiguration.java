package integration.repository.mongo;

import javax.validation.constraints.NotNull;

import lombok.Data;

import com.mongodb.MongoClient;

/**
 * <P>
 * Encapsulates information needed to create a {@link MongoClient}.
 * </p>
 */
@Data
public class MongoDbConfiguration {

    /**
     * A URI in the format:
     * 
     * <pre>
     * mongodb://host:port
     * </pre>
     */
    private final String mongoClientUri;

    /**
     * Optional name of the database.
     */
    private final String dbName;


    /**
     * The mongo client URI must be supplied but other values are optional.
     * 
     * @param mongoClientUri
     *            A URI in the format: <code>mongodb://host:port</code>
     * @param dbName
     *            Optional name of the database.
     */
    public MongoDbConfiguration(@NotNull String mongoClientUri, String dbName) {
        this.mongoClientUri = mongoClientUri;
        this.dbName = dbName;
    }

    // ////////////////// GETTERS AND SETTERS //////////////////////

    public String getMongoClientUri() {
        return mongoClientUri;
    }

    public String getDbName() {
        return dbName;
    }

}