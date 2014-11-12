package server.dw.resource;

import io.dropwizard.jersey.errors.ErrorMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link MessageBodyWriter} that can output a {@link ErrorMessage} when the output MIME type is "text/plain". Without
 * this, any {@link RuntimeException} that bubbles up to a resource that produces "text/plain" will not be reported back
 * to the client (the one that made the request). See for more information:
 * 
 * <pre>
 * http://stackoverflow.com/questions/26138516/dropwizard-error-messages-from-jersey
 * </pre>
 */
@Provider
@Produces(MediaType.TEXT_PLAIN)
public class ErrorMessageBodyWriter implements MessageBodyWriter<ErrorMessage> {

    private Logger logger = LoggerFactory.getLogger(ErrorMessageBodyWriter.class);

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {

        boolean isWritable = ErrorMessage.class.isAssignableFrom(type);

        if (!isWritable) {
            if (logger.isDebugEnabled()) {
                logger.debug("Not matching MessageBodyWriter {}", ErrorMessageBodyWriter.class.getName());
            }
        }

        return isWritable;
    }

    @Override
    public long getSize(ErrorMessage t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(ErrorMessage t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
            WebApplicationException {
        String message = t.getMessage();
        entityStream.write(message.getBytes(Charsets.UTF_8));
    }

}
