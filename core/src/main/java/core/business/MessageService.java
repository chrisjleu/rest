package core.business;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mongodb.QueryBuilder;

import core.integration.MongoFactory;
import core.model.Message;

/**
 * <p>
 * Does all the things related to messages (for now).
 * <p>
 * The current implementation of this service class is tightly coupled to MongoDb. This is fine for now but in theory
 * this could be factored out when the data access part of the application is factored out.
 */
@Service
public class MessageService {

    Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final MongoFactory mongoFactory;

    private JacksonDBCollection<Message, String> messageColl;

    @Inject
    public MessageService(MongoFactory mongofactory) {
        this.mongoFactory = mongofactory;
    }

    @PostConstruct
    void init() {
        try {
            // TODO Need to understand how the DBCollection should be accessed/initialized properly. Not sure if this is
            // the correct place
            this.messageColl = JacksonDBCollection.wrap(mongoFactory.buildColl(), Message.class, String.class);
        } catch (Exception e) {
            throw new RuntimeException("Unable to construct " + MessageService.class.getName(), e);
        }
    }

    /**
     * Add the given {@link Message} to the database.
     * 
     * @param message
     * @return The id of the message as stored in the database.
     */
    public Message add(@Valid Message message) {
        logger.debug("Inserting {}", message);

        String generatedId = messageColl.insert(message).getSavedId();
        message.setId(generatedId);

        return enhanceMessage(message);
    }

    public void dropCollection() {
        logger.warn("Dropping collection \"{}\"", messageColl.getName());
        messageColl.drop();
    }

    /**
     * Counts the number of messages in the repository.
     * 
     * @return
     */
    public long count() {
        return messageColl.getCount();
    }

    /**
     * Returns a list of all messages that exist, up to a sensible maximum number.
     * 
     * @return
     */
    public List<Message> all() {
        DBCursor<Message> dbCursor = messageColl.find().limit(100);
        return toMessageList(dbCursor);
    }

    /**
     * Returns all messages from a point within a radius.
     * 
     * @return
     */
    public List<Message> allInRange(double ln, double lt, double maxDistance) {
        DBCursor<Message> dbCursor = messageColl.find(QueryBuilder.start().nearSphere(ln, lt, maxDistance).get());
        return toMessageList(dbCursor);
    }

    private List<Message> toMessageList(DBCursor<Message> dbCursor) {
        List<Message> messages = new ArrayList<Message>();
        while (dbCursor.hasNext()) {
            Message message = dbCursor.next();
            messages.add(enhanceMessage(message));
        }
        return messages;
    }

    /**
     * Adds any other fields that need to be processed to the message.
     */
    private Message enhanceMessage(Message message) {
        ObjectId mongoId = new ObjectId(message.getId());
        message.setCreationDate(mongoId.getDate());
        return message;
    }

}
