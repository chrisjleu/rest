package core.service;

import integration.api.repository.Repository;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import core.model.Message;

/**
 * <p>
 * Does all the things related to messages (for now).
 * </p>
 */
@Service
public class MessageService {

    Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final Repository<Message> repository;

    @Inject
    public MessageService(Repository<Message> repository) {
        this.repository = repository;
    }

    /**
     * Add the given {@link Message} to the database.
     * 
     * @param message
     * @return The saved {@link Message}.
     */
    public Message add(@Valid Message message) {
        logger.debug("Inserting {}", message);

        Message savedMessage = repository.insert(message);

        return enhanceMessage(savedMessage);
    }

    /**
     * Deletes all messages from the repository.
     */
    public void dropAll() {
        logger.warn("Deleting all documents of type \"{}\"", repository.getType());
        repository.drop();
    }

    /**
     * Counts the number of messages in the repository.
     * 
     * @return
     */
    public long count() {
        return repository.count();
    }

    /**
     * Returns a list of all messages that exist, up to a sensible maximum number.
     * 
     * @return
     */
    public List<Message> all() {
        return enhanceMessages(repository.all());
    }

    /**
     * Returns all messages from a point within a radius.
     * 
     * @return
     */
    public List<Message> allInRange(double ln, double lt, double maxDistance) {
        return enhanceMessages(repository.allInRange(ln, lt, maxDistance));
    }

    /**
     * Computes any other fields to the saved {@link Message}s for convenience.
     */
    private List<Message> enhanceMessages(List<Message> messages) {
        for (Message message : messages) {
            enhanceMessage(message);
        }

        return messages;
    }

    /**
     * Computes any other fields to the saved {@link Message} for convenience.
     */
    private Message enhanceMessage(Message message) {
        ObjectId mongoId = new ObjectId(message.getId());
        message.setCreationDate(mongoId.getDate());
        return message;
    }

}
