package core.business;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import core.model.Message;

@RunWith(MockitoJUnitRunner.class)
public class TestMessageService {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private JacksonDBCollection<Message, String> messageCollection;

    @Mock
    private WriteResult<Message, String> result;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests that the <code>addMessage</code> method returns the Id of the message when it was able to successfully
     * insert the message into the database.
     */
    @Test
    public void test_that_correct_id_returned_when_a_message_is_added() {

        // Given the actual insertion of the message to the DB (done via a JacksonDBCollection) is mocked...
        Message incomingMessage = new Message("This is a message", 1.0, 1.0);
        ObjectId objectId = ObjectId.get();
        String id = objectId.toString();
        
        when(messageCollection.insert(incomingMessage)).thenReturn(result);
        when(messageCollection.insert(incomingMessage).getSavedId()).thenReturn(id);

        // ...when making a call to add the message (to the database)...
        Message savedMessage = messageService.add(incomingMessage);

        // ...then the correct id of the message should be returned.
        assertNotNull(savedMessage);
        assertThat(savedMessage.getId(), equalTo(id));
        assertThat(savedMessage.getLatidude(), equalTo(1.0));
        assertThat(savedMessage.getLongitude(), equalTo(1.0));
        assertThat(savedMessage.getCreationDate(), equalTo(objectId.getDate()));
    }

}
