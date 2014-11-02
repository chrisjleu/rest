package core.business;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import core.integration.MongoDbConfiguration;
import core.integration.MongoFactory;

@RunWith(MockitoJUnitRunner.class)
public class TestMongoFactory {

    @InjectMocks
    private MongoFactory mongoFactory;

    @Mock
    private MongoDbConfiguration mongoDbConfiguration;

    @BeforeClass
    public static void setUp() {
    }
    
    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void close() {
        mongoFactory.closeClient();
    }
    
    @Test
    public void test_database_name_is_configured_correctly_if_specified_in_the_connection_uri() throws Exception {

        // Given...
        when(mongoDbConfiguration.getMongoClientUri()).thenReturn("mongodb://localhost:27017/database");
        when(mongoDbConfiguration.getDbName()).thenReturn("myDb");

        // ...when
        mongoFactory.configure(); // Call the @PostConstruct method
        String databaseName = mongoFactory.getDatabaseName();

        // ...then
        assertNotNull(databaseName);
        assertThat(databaseName, equalTo("database"));
    }

    @Test
    public void test_database_name_is_configured_correctly_if_NOT_specified_in_the_connection_uri() throws Exception {

        // Given...
        when(mongoDbConfiguration.getMongoClientUri()).thenReturn("mongodb://localhost:27017");
        when(mongoDbConfiguration.getDbName()).thenReturn("myDb");

        // ...when
        mongoFactory.configure(); // Call the @PostConstruct method
        String databaseName = mongoFactory.getDatabaseName();

        // ...then
        assertNotNull(databaseName);
        assertThat(databaseName, equalTo("myDb"));
    }

}
