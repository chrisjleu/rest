package core.business;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import integration.repository.MongoDbRepository;

import java.util.ArrayList;
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

public class TestMessageService {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private MongoDbRepository<Message> repository;

    private Message repositoryMessage;

    private ObjectId savedMessageId;

    private List<Message> repoMessages;

    private static final String MESSAGE = "This is a message";
    private static final double LATITUDE = 1.0;
    private static final double LONGITUDE = 1.0;
    private static final double RANGE = 200;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        // Represents a message that exists in the repository
        repositoryMessage = new Message(MESSAGE, LATITUDE, LONGITUDE);
        savedMessageId = ObjectId.get();
        repositoryMessage.setId(savedMessageId.toString());

        // A list of messages (only one it it for the purposes of testing)
        repoMessages = new ArrayList<Message>(1);
        repoMessages.add(repositoryMessage);

        // Make the repository return the message when various methods are called
        when(repository.all()).thenReturn(repoMessages);
        when(repository.allInRange(LONGITUDE, LATITUDE, RANGE)).thenReturn(repoMessages);
        when(repository.count()).thenReturn((long) repoMessages.size());
    }

    @Test
    public void test_message_gets_added_to_the_repository() {

        // Given
        Message incoming = new Message(MESSAGE, LATITUDE, LONGITUDE);
        when(repository.insert(incoming)).thenReturn(repositoryMessage);

        // when
        Message message = messageService.add(incoming);

        // then
        assertThat(message, is(asExpected(message)));
    }

    @Test
    public void count_all_messages_in_the_repository() {
        // Given
        // See init() method

        // when
        long reportedMessageCount = messageService.count();

        // then
        assertThat(reportedMessageCount, is((long) repoMessages.size()));
    }

    @Test
    public void list_all_messages_in_the_repository() {
        // Given
        // See init() method

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
    public void list_all_in_range_messages_in_the_repository() {
        // Given
        // See init() method

        // when
        List<Message> messages = messageService.allInRange(LONGITUDE, LATITUDE, RANGE);

        // then
        assertNotNull(messages);
        assertThat(messages, is(not(empty())));
        assertThat(messages, hasSize(1));
        
        Message message = messages.get(0);
        assertThat(message, is(asExpected(message)));
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
                assertThat(message.getId(), equalTo(repositoryMessage.getId()));
                assertThat(message.getValue(), equalTo(MESSAGE));
                assertThat(message.getLatidude(), equalTo(LATITUDE));
                assertThat(message.getLongitude(), equalTo(LONGITUDE));
                assertThat(message.getCreationDate(), is(savedMessageId.getDate()));
                return true;
            }
        };
    }

}
