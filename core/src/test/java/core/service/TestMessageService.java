package core.service;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import integration.api.model.InsertResult;
import integration.api.model.message.MessageDao;
import integration.repository.mongo.MongoDbRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import core.model.Message;
import core.model.request.CreateMessageRequest;

public class TestMessageService {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MongoDbRepository<MessageDao> repository;

    @Mock
    private InsertResult<MessageDao> insertResult;

    @Mock
    private MessageDao messageDao;

    // Values that form the message in the repository
    private static final ObjectId MESSAGE_ID = ObjectId.get();
    private static final Date CREATION_DATE = MESSAGE_ID.getDate();
    private static final String MESSAGE = "This is a message";
    private static final double LATITUDE = 1.0;
    private static final double LONGITUDE = 1.0;
    private static final double RANGE = 200;

    private List<MessageDao> repoMessages;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        when(repository.getType()).thenReturn(MessageDao.class);

        // Mock a message DAO that has already been persisted in the repository
        when(messageDao.getId()).thenReturn(MESSAGE_ID.toString());
        when(messageDao.getValue()).thenReturn(MESSAGE);
        when(messageDao.getLatidude()).thenReturn(LATITUDE);
        when(messageDao.getLongitude()).thenReturn(LONGITUDE);

        // A list of messages (only one it it for the purposes of testing)
        repoMessages = new ArrayList<MessageDao>(1);
        repoMessages.add(messageDao);
    }

    @Test
    public void test_create_message_request_is_converted_properly_to_message_dao() {
        // Given
        CreateMessageRequest request = new CreateMessageRequest(MESSAGE, LATITUDE, LONGITUDE);

        // when
        MessageDao dao = messageService.toMessageDao(request);

        // then
        assertThat(dao, is(notNullValue()));
        assertThat(dao.getId(), is(nullValue())); // Null since it is created by the repository on insert
        assertThat(dao.getValue(), is(equalTo(MESSAGE)));
        assertThat(dao.getLatidude(), is(equalTo(LATITUDE)));
        assertThat(dao.getLongitude(), is(equalTo(LONGITUDE)));

    }

    @Test
    public void test_message_daos_are_converted_to_list_of_message_domain_objects() {
        // Given
        // See init() method

        // when
        List<Message> messages = messageService.toMessageList(repoMessages);

        // then
        assertThat(messages, is(notNullValue()));
        assertThat(messages, is(not(empty())));
        assertThat(messages, hasSize(1));

        Message message = messages.get(0);
        assertThat(message, is(asExpected(message)));
    }

    @Test
    public void test_message_gets_added_to_the_repository() {

        // Given
        CreateMessageRequest request = new CreateMessageRequest(MESSAGE, LATITUDE, LONGITUDE);
        MessageDao messageDaoToSave = messageService.toMessageDao(request);
        when(repository.insert(messageDaoToSave)).thenReturn(insertResult);
        when(insertResult.getInserted()).thenReturn(messageDao);

        // when
        Message message = messageService.add(request);

        // then
        assertThat(message, is(asExpected(message)));
    }

    @Test
    public void test_that_message_count_method_counts_properly() {
        // Given
        long actualMessageCount = (long) repoMessages.size();
        when(repository.count()).thenReturn(actualMessageCount);

        // when
        long reportedMessageCount = messageService.count();

        // then
        assertThat(reportedMessageCount, is(equalTo(actualMessageCount)));
    }

    @Test
    public void test_delete_all_messages_from_repository() {
        // Given
        when(repository.count()).thenReturn(0l);

        // when
        messageService.dropAll();

        // then
        long messageCount = messageService.count();
        assertThat(0l, is(equalTo(messageCount)));
    }

    @Test
    public void list_all_messages_in_the_repository() {
        // Given
        when(repository.all()).thenReturn(repoMessages);

        // when
        List<Message> messages = messageService.all();

        // then
        assertNotNull(messages);
        assertThat(messages, is(not(empty())));
        assertThat(messages, hasSize(1));

        Message message = messages.get(0);
        assertThat(message, is(asExpected(message)));
    }

    @Test
    public void list_all_messages_that_are_within_range() {
        // Given
        when(repository.allInRange(LONGITUDE, LATITUDE, RANGE)).thenReturn(repoMessages);

        // when
        List<Message> messages = messageService.allInRange(LONGITUDE, LATITUDE, RANGE);

        // then
        assertNotNull(messages);
        assertThat(messages, is(not(empty())));
        assertThat(messages, hasSize(1));

        Message message = messages.get(0);
        assertThat(message, is(asExpected(message)));
    }

    @Test
    public void check_messages_not_returned_when_out_of_range() {
        // Given
        when(repository.allInRange(LONGITUDE, LATITUDE, RANGE)).thenReturn(repoMessages);

        // when
        List<Message> messages = messageService.allInRange(LONGITUDE+1, LATITUDE+1, RANGE);

        // then
        assertNotNull(messages);
        assertThat(messages, is(empty()));
        assertThat(messages, hasSize(0));
    }
    
    /**
     * Tests that a message has all the values that are expected for these tests.
     * 
     * @param message
     * @return
     */
    private Matcher<Message> asExpected(final Message message) {
        return new TypeSafeMatcher<Message>() {
            @Override
            public void describeTo(final Description description) {
                description.appendText("Should be ").appendValue(message);
            }

            @Override
            protected void describeMismatchSafely(final Message message, final Description mismatchDescription) {
                mismatchDescription.appendText(" was ").appendValue(message);
            }

            @Override
            protected boolean matchesSafely(final Message message) {
                assertThat(message, is(notNullValue()));
                assertThat(message.getId(), equalTo(MESSAGE_ID.toString()));
                assertThat(message.getValue(), equalTo(MESSAGE));
                assertThat(message.getLatidude(), equalTo(LATITUDE));
                assertThat(message.getLongitude(), equalTo(LONGITUDE));
                assertThat(message.getCreationDate(), is(CREATION_DATE));
                return true;
            }
        };
    }

}
