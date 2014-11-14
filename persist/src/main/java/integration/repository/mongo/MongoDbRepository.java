package integration.repository.mongo;

import integration.api.model.InsertResult;
import integration.api.repository.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.QueryBuilder;

/**
 * <p>
 * An implementation of the {@link Repository} with MongoDb. This particular implementation uses the MongoJack library.
 * Could consider moving to Spring Data eventually with all of this.
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
    
    public MongoDbRepository(Class<T> typeParameterClass, MongoFactory mongofactory) {
        this.typeParameterClass = typeParameterClass;
        this.mongoFactory = mongofactory;
    }

    @Override
    public Class<T> getType() {
        return typeParameterClass;
    }
    
    @PostConstruct
    void init() {
        try {
            // TODO Is this how to initialize the DB with MongoJack?
            this.collection = JacksonDBCollection.wrap(mongoFactory.buildColl(), typeParameterClass, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to construct " + MongoDbRepository.class.getName(), e);
        }
    }

    @Override
    public InsertResult<T> insert(T insertable) {
        logger.debug("Inserting {}", insertable);
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
