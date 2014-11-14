package integration.repository.mongo;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import integration.api.model.InsertResult;
import integration.api.model.message.MessageDao;

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

@RunWith(MockitoJUnitRunner.class)
public class TestMongoDbRepository {

    @InjectMocks
    private MongoDbRepository<MessageDao> mongoDbRepository;

    @Mock
    private JacksonDBCollection<MessageDao, String> messageCollection;

    @Mock
    private WriteResult<MessageDao, String> result;

    @Mock
    MessageDao incomingMessage;

    ObjectId mongoObjectId = new ObjectId();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Tests that a message is inserted into a Mongo repository.
     */
    @Test
    public void test_message_insert_success_case() {

        // Given the actual insertion of the message to the DB (done via a JacksonDBCollection) is mocked...
        when(messageCollection.insert(incomingMessage)).thenReturn(result);
        when(messageCollection.insert(incomingMessage).getSavedObject()).thenReturn(incomingMessage);
        // when(messageCollection.insert(incomingMessage).getSavedId()).thenReturn(id);
        when(incomingMessage.getId()).thenReturn(mongoObjectId.toString());
        when(incomingMessage.getValue()).thenReturn("This is a message");
        when(incomingMessage.getLatidude()).thenReturn(1.0);
        when(incomingMessage.getLongitude()).thenReturn(1.0);

        // ...when making a call to add the message (to the database)...
        InsertResult<MessageDao> result = mongoDbRepository.insert(incomingMessage);

        // ...then
        assertThat(result, is(notNullValue()));

        MessageDao savedMessage = result.getInserted();
        assertThat(savedMessage, is(notNullValue()));
        assertThat(savedMessage.getId(), is(notNullValue()));
        assertThat(savedMessage.getValue(), equalTo("This is a message"));
        assertThat(savedMessage.getLatidude(), equalTo(1.0));
        assertThat(savedMessage.getLongitude(), equalTo(1.0));
    }

}
