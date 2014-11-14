package core.model;

import integration.api.model.message.MessageDao;

import java.util.Date;

import lombok.Data;

import org.bson.types.ObjectId;

/**
 * A message POJO.
 */
@Data
public class Message {

    private final String id;

    private Date creationDate;

    private final String value;

    private final double longitude;

    private final double latidude;


    public Message(String id, String value, double lat, double lon) {
        this.id = id;
        this.value = value;
        this.latidude = lat;
        this.longitude = lon;
    }

    public static MessageDao toDao(Message message) {
        return new MessageDao(message.value, message.latidude, message.longitude);
    }

    public static Message fromDao(MessageDao dao) {
        Message message = new Message(dao.getId(), dao.getValue(), dao.getLatidude(), dao.getLongitude());
        ObjectId mongoId = new ObjectId(message.getId());
        message.setCreationDate(mongoId.getDate());
        return message;
    }

    Message enhance(Message message, MessageDao dao) {
        ObjectId mongoId = new ObjectId(message.getId());
        message.setCreationDate(mongoId.getDate());
        message.setCreationDate(creationDate);
        return message;
    }
}
