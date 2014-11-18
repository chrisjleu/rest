package integration.config;

import integration.api.model.message.MessageDao;
import integration.api.model.user.auth.AccountDao;
import integration.api.repository.Repository;
import integration.repository.mongo.MongoDbConfiguration;
import integration.repository.mongo.MongoDbRepository;
import integration.repository.mongo.MongoFactory;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@ComponentScan(basePackages = { "integration.repository", "integration.service" })
@PropertySources({
        @PropertySource(name = "default", value = "classpath:default.properties"),
        @PropertySource(name = "override", value = "file:///${user.home}/override.properties", ignoreResourceNotFound = true) })
public class AppConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
        pspc.setLocalOverride(true); // Now local (system) properties override property file values
        return pspc;
    }

    @Value("${MONGO_URI}")
    private String mongoDbConnectionUri;

    @Value("${mongo.db.name}")
    private String mongoDbName;

    @Bean
    public MongoDbConfiguration mongoDbConfiguration() throws Exception {
        return new MongoDbConfiguration(mongoDbConnectionUri, mongoDbName);
    }

    // NOTE: Ensure that mongoDbConnectionUri is resolved before this is injected
    @Inject
    MongoFactory mongoFactory;

    @Bean
    public Repository<MessageDao> messageRepositoryFactory() {
        return new MongoDbRepository<MessageDao>(MessageDao.class, mongoFactory);
    }

    @Bean
    public Repository<AccountDao> userAccountRepositoryFactory() {
        return new MongoDbRepository<AccountDao>(AccountDao.class, mongoFactory);
    }

}
