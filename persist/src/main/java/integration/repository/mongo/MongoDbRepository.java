package integration.repository.mongo;

import integration.api.model.InsertResult;
import integration.api.model.PropertyValuePair;
import integration.api.repository.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.MongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DBCollection;
import com.mongodb.QueryBuilder;

/**
 * <p>
 * An implementation of the {@link Repository} with MongoDb. This particular implementation uses the MongoJack library.
 * Each instance of this class operates on only one collection. Might consider moving to Spring Data eventually with all
 * of this.
 * </p>
 * 
 * @param <T>
 *            The class to save as a MongoDb document.
 */
public class MongoDbRepository<T> implements Repository<T> {

    Logger logger = LoggerFactory.getLogger(MongoDbRepository.class);

    private final MongoFactory mongoFactory;

    private JacksonDBCollection<T, String> collection;

    private final Class<T> typeParameterClass;

    MongoFactory mongofactory;

    /**
     * Constructs a {@link Repository} with a Mongo DB implementation.
     * 
     * @param clazz
     *            The class that this repository operates on. This has a one-to-one mapping with a Mongo DB Collection.
     * @param mongofactory
     *            Required to create the MongoDB collection.
     */
    public MongoDbRepository(Class<T> clazz, MongoFactory mongofactory) {
        this.typeParameterClass = clazz;
        this.mongoFactory = mongofactory;
    }

    @Override
    public Class<T> getType() {
        return typeParameterClass;
    }

    // TODO Is this the best way to initialize the collection/db with MongoJack?
    @PostConstruct
    void init() {
        try {
            String collectionName = null;
            if (typeParameterClass.isAnnotationPresent(MongoCollection.class)) {
                MongoCollection mongoCollection = typeParameterClass.getAnnotation(MongoCollection.class);
                collectionName = mongoCollection.name();
            } else {
                collectionName = typeParameterClass.getSimpleName();
            }

            DBCollection coll = mongoFactory.buildColl(collectionName);
            this.collection = JacksonDBCollection.wrap(coll, typeParameterClass, String.class);
            logger.debug("Repository initilaized for collection \"{}\"", collection.getName());
        } catch (Exception e) {
            throw new RuntimeException("Unable to construct " + MongoDbRepository.class.getName(), e);
        }
    }

    @Override
    public T find(PropertyValuePair... pvps) {
        Query[] queryExpressions = new Query[pvps.length];
        for (int i = 0; i < pvps.length; i++) {
            queryExpressions[i] = DBQuery.is(pvps[i].getPropertyName(), pvps[i].getPropertyValue());
        }
        Query matchAllValuesQuery = DBQuery.and(queryExpressions);
        T document = collection.findOne(matchAllValuesQuery);

        if (document == null) {
            logger.debug("Could not match {} in collection {}", Arrays.toString(pvps), collection.getName());
        } else {
            logger.debug("Found {} in collection {}", document, collection.getName());
        }

        return document;
    }

    @Override
    public InsertResult<T> insert(T insertable) {
        logger.debug("Inserting {} into {}", insertable, collection.getName());
        T inserted = collection.insert(insertable).getSavedObject();
        return new InsertResult<T>(inserted);
    }

    @Override
    public void drop() {
        logger.warn("Dropping collection \"{}\"", collection.getName());
        collection.drop();
    }

    @Override
    public long count() {
        return collection.getCount();
    }

    @Override
    public List<T> all() {
        DBCursor<T> dbCursor = collection.find().limit(100);
        logger.debug("Found {} documents in collection {}", dbCursor.size(), collection.getName());
        return toDocumentList(dbCursor);
    }

    @Override
    public List<T> allInRange(double ln, double lt, double maxDistance) {
        DBCursor<T> dbCursor = collection.find(QueryBuilder.start().nearSphere(ln, lt, maxDistance).get());
        return toDocumentList(dbCursor);
    }

    private List<T> toDocumentList(DBCursor<T> dbCursor) {
        List<T> documents = new ArrayList<T>();
        while (dbCursor.hasNext()) {
            T document = dbCursor.next();
            documents.add(document);
        }
        return documents;
    }

}
