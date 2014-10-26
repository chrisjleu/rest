package core.business;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.Valid;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
     * Counts the number of messages in the repository.
     * 
     * @return
     */
    public int count() {
        return messageColl.find().count();
    }

    /**
     * Returns a list of all messages that exist, up to a sensible maximum number.
     * 
     * @return
     */
    public List<Message> all() {
        DBCursor<Message> dbCursor = messageColl.find().limit(100);
        List<Message> messages = new ArrayList<Message>();
        while (dbCursor.hasNext()) {
            Message message = dbCursor.next();
            messages.add(message);
        }
        return messages;
    }

    /**
     * Add the given {@link Message} to the database.
     * 
     * @param message
     * @return The id of the message as stored in the database.
     */
    public String add(@Valid Message message) {
        logger.debug("Adding {}", message);
        return messageColl.insert(message).getSavedId();
    }

    public void dropCollection() {
        logger.warn("Dropping collection \"{}\"", messageColl.getName());
        messageColl.drop();
    }

}
