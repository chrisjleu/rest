package core.service;

import integration.api.model.InsertResult;
import integration.api.model.message.MessageDao;
import integration.api.repository.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import core.model.Message;
import core.model.request.CreateMessageRequest;

/**
 * <p>
 * Handles all {@link Message} related things.
 * </p>
 */
@Service
public class MessageService {

    Logger logger = LoggerFactory.getLogger(MessageService.class);

    private final Repository<MessageDao> repository;

    @Inject
    public MessageService(Repository<MessageDao> repository) {
        this.repository = repository;
    }

    /**
     * Add the given {@link Message} to the database.
     * 
     * @param message
     * @return The saved {@link Message}.
     */
    public Message add(@Valid CreateMessageRequest request) {
        logger.debug("Proceeding to create message from request: {}", request);

        MessageDao messageDao = toMessageDao(request);

        InsertResult<MessageDao> insertResult = repository.insert(messageDao);

        Message message = Message.fromDao(insertResult.getInserted());

        logger.debug("Created message: {}", message);

        return message;
    }

    /**
     * Deletes all messages from the repository.
     */
    public void dropAll() {
        logger.warn("Deleting all documents of type \"{}\"", repository.getType().getName());
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
        return toMessageList(repository.all());
    }

    /**
     * Returns all messages from a point within a radius.
     * 
     * @return
     */
    public List<Message> allInRange(double ln, double lt, double maxDistance) {
        return toMessageList(repository.allInRange(ln, lt, maxDistance));
    }

    /**
     * Converts a {@link CreateMessageRequest} to a {@link MessageDao}.
     * 
     * @param request
     * @return
     */
    MessageDao toMessageDao(CreateMessageRequest request) {
        return new MessageDao(request.getValue(), request.getLatidude(), request.getLongitude());
    }

    /**
     * Converts Message Daos to the {@link Message} domain object.
     */
    List<Message> toMessageList(List<MessageDao> daos) {
        List<Message> messages = new ArrayList<Message>();
        if (daos != null && daos.size() > 0) {
            for (MessageDao dao : daos) {
                messages.add(Message.fromDao(dao));
            }
        }

        return messages;
    }

}
