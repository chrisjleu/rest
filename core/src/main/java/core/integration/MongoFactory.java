package core.integration;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

import core.util.HostAndPort;

/**
 * <p>
 * An object of this class creates a single instance of the <code>MongoClient</code> object.
 * </p>
 */
@Component
public class MongoFactory {

    /**
     * The configuration POJO needed in order to build a {@link MongoClient}.
     */
    private final MongoDbConfiguration mongoConfig;

    @Inject
    public MongoFactory(MongoDbConfiguration mongoDbConfiguration) {
        mongoConfig = mongoDbConfiguration;
    }

    /**
     * The mongo API documentation for <a href="https://api.mongodb.org/java/current/com/mongodb/MongoClient.html">
     * MongoClient</a> states that there should only be one object per JVM, so this property is only set once.
     */
    private MongoClient mongoClient;


    /**
     * Builds the MongoClient from a set of connections specified in the configuration file.
     * 
     * @return A Mongo API {@code MongoClient} object.
     * @throws {@link UnknownHostException} Thrown if the server can not be found.
     */
    @PostConstruct
    public void buildClient() throws UnknownHostException {
        List<HostAndPort> hosts = mongoConfig.getServerAddresses();
        List<ServerAddress> serverAddresses = new ArrayList<>(hosts.size());
        for (HostAndPort hostAndPort : hosts) {
            serverAddresses.add(new ServerAddress(hostAndPort.getHost(), hostAndPort.getPort()));
        }

        this.mongoClient = new MongoClient(serverAddresses);
    }

    /**
     * Closes the {@link MongoClient} before this component is destroyed.
     */
    @PreDestroy
    public void closeClient() {
        mongoClient.close();
    }

    /**
     * Gets the instance of the {@link MongoClient} - there is only one.
     * 
     * @return The {@link MongoClient} instance of which there is only one per factory.
     */
    public MongoClient instance() {
        return mongoClient;
    }

    /**
     * Builds a Mongo {@code DB} object from connection and db info set in a configuration file.
     * 
     * @return A Mongo Java API {@code DB} object.
     * @throws Exception
     * @throws {@link UnknownHostException} Thrown if the server can not be found.
     * @throws {@link com.eeb.dropwizardmongo.exceptions.NullDBNameException} Throw in the db name is null.
     */
    public DB buildDB() throws Exception {
        if (mongoConfig.getDbName() == null)
            throw new Exception("DB name is null");

        return mongoClient.getDB(mongoConfig.getDbName());
    }

    /**
     * Builds a Mongo {@code DBCollection} object from connection, db, and collection information set in a configuration
     * file.
     * 
     * @return A Mongo Java API {@code DBCollection} object.
     * @throws {@link UnknownHostException} Thrown if the server can not be found.
     * @throws {@link com.eeb.dropwizardmongo.exceptions.NullDBNameException} Throw in the db name is null.
     * @throws {@link NullCollectionNameException} Thrown if the collection name is null.
     */
    public DBCollection buildColl() throws Exception {
        if (mongoConfig.getDbName() == null)
            throw new Exception("DB name is null");

        if (mongoConfig.getCollName() == null)
            throw new Exception("Collection name is null");

        final DB db = buildDB();
        return db.getCollection(mongoConfig.getCollName());

    }
}