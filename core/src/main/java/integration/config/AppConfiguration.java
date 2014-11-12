package integration.config;

import integration.api.repository.Repository;
import integration.repository.MongoDbConfiguration;
import integration.repository.MongoDbRepository;
import integration.repository.MongoFactory;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import core.model.Message;
import core.model.User;

@Configuration
@ComponentScan(basePackages = { "integration" })
@PropertySources({ @PropertySource("classpath:default.properties"),
        @PropertySource(value = "file:/${user.home}/override.properties", ignoreResourceNotFound = true) })
public class AppConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setLocalOverride(true); // Now local (system) properties override property file values
        return pspc;
    }

    @Value("${MONGO_URI}")
    private String mongoDbConnectionUri;

    @Bean
    public MongoDbConfiguration mongoDbConfiguration() throws Exception {
        return new MongoDbConfiguration(mongoDbConnectionUri, "mydb", "messages");
    }

    // NOTE: Ensure that mongoDbConnectionUri is resolved before this is injected
    @Inject
    MongoFactory mongoFactory;

    @Bean
    public Repository<Message> messageRepositoryFactory() {
        return new MongoDbRepository<Message>(Message.class, mongoFactory);
    }

    @Bean
    public Repository<User> userRepositoryFactory() {
        return new MongoDbRepository<User>(User.class, mongoFactory);
    }
}
