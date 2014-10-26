package core.integration;

import java.util.List;

import com.mongodb.MongoClient;

import core.util.HostAndPort;

/**
 * <P>
 * All the information needed to create a {@link MongoClient} are stored here
 * </p>
 */
public class MongoDbConfiguration {

    /**
     * MongoDB locations.
     */
    private List<HostAndPort> serverAddresses;

    /**
     * Optional name of the database. This property is required to use the dbBuild method.
     */
    private String dbName;

    /**
     * Optional name of the collection to be set. The property is required to use the collBuild method.
     */
    private String collName;

    // ////////////////// GETTERS AND SETTERS //////////////////////

    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<HostAndPort> getServerAddresses() {
        return serverAddresses;
    }

    public void setServerAddresses(List<HostAndPort> serverAddresses) {
        this.serverAddresses = serverAddresses;
    }
}