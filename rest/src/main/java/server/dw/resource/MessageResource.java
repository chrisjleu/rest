package server.dw.resource;

import io.dropwizard.auth.Auth;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.Message;
import api.representations.User;

import com.codahale.metrics.annotation.Timed;

import core.model.request.CreateMessageRequest;
import core.service.MessageService;

/**
 * <p>
 * Resources map various aspects of incoming HTTP requests to POJOs and perform the reverse process for outgoing HTTP
 * responses. This part is therefore very much the API to the application. The methods here describe what can resources
 * are available. The method parameters describe of course what must be provided in order to obtain the resource and
 * return types are the <a
 * href="https://dropwizard.github.io/dropwizard/manual/core.html#man-core-representations">representations</a> of the
 * resource being requested (in the case of DropWiard, it's likely to be a JSON representation of the object).
 * </p>
 * <p>
 * Resources related to messages.
 * </p>
 */
@Path("/m")
@Produces(value = MediaType.APPLICATION_JSON)
@Consumes(value = MediaType.APPLICATION_JSON)
public class MessageResource {

    Logger logger = LoggerFactory.getLogger(MessageResource.class);

    private final MessageService messageService;

    public MessageResource(MessageService messageService) {
        this.messageService = messageService;
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    @Path("/count")
    public long count() {
        logger.debug("Request for count received");
        return messageService.count();
    }

    @GET
    @Produces(value = MediaType.APPLICATION_JSON)
    @Timed
    public List<Message> all() {
        List<core.model.Message> coreMessages = messageService.all();
        List<Message> messages = new ArrayList<>();
        if (coreMessages != null) {
            for (core.model.Message message : coreMessages) {
                messages.add(Message.fromModel(message));
            }
        }

        return messages;
    }

    /**
     * Send for example:
     * 
     * <pre>
     * curl -X POST -H "Content-Type: application/json" -d '{"v":"This is a message", "ln":1.0, "lt":1.0}' http://localhost:8080/m
     * </pre>
     * 
     * <p>The {@link User} is injected into the method, if authorized, courtesy of the {@link Auth} annotation</p>
     * 
     * @param m
     *            The message that the user has sent.
     * @return
     */
    @POST
    @Timed
    public Response send(@Auth User user, @Valid Message m) {
        logger.debug("Recieved message {}", m);
        CreateMessageRequest req = new CreateMessageRequest(m.getValue(), m.getLatidude(), m.getLongitude());
        messageService.add(req);
        return Response.noContent().build();
    }

    @DELETE
    @Timed
    public Response dropCollection() {
        messageService.dropAll();
        return Response.noContent().build();
    } 

}
