package integration.repository;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import integration.repository.MongoDbRepository;

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
public class TestMongoDbRepository {

    @InjectMocks
    private MongoDbRepository<Message> mongoDbRepository;

    @Mock
    private JacksonDBCollection<Message, String> messageCollection;

    @Mock
    private WriteResult<Message, String> result;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests that a message is inserted into a Mongo repository.
     */
    @Test
    public void test_that_correct_id_returned_when_a_message_is_added() {

        // Given the actual insertion of the message to the DB (done via a JacksonDBCollection) is mocked...
        Message incomingMessage = new Message("This is a message", 1.0, 1.0);
        ObjectId objectId = ObjectId.get();
        String id = objectId.toString();
        incomingMessage.setId(id);

        when(messageCollection.insert(incomingMessage)).thenReturn(result);
        when(messageCollection.insert(incomingMessage).getSavedObject()).thenReturn(incomingMessage);
        // when(messageCollection.insert(incomingMessage).getSavedId()).thenReturn(id);

        // ...when making a call to add the message (to the database)...
        Message savedMessage = mongoDbRepository.insert(incomingMessage);

        // ...then
        assertNotNull(savedMessage);
        assertThat(savedMessage.getId(), equalTo(id));
        assertThat(savedMessage.getValue(), equalTo("This is a message"));
        assertThat(savedMessage.getLatidude(), equalTo(1.0));
        assertThat(savedMessage.getLongitude(), equalTo(1.0));
        assertThat(savedMessage.getCreationDate(), is(nullValue()));
    }

}
