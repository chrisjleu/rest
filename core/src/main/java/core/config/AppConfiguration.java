package core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import core.integration.MongoDbConfiguration;

@Configuration
@ComponentScan(basePackages={"core"})
public class AppConfiguration {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
    
    @Value("${MONGOLAB_URI}")
    private String mongoDbConnectionUri;
    
    @Bean
    public MongoDbConfiguration mongoDbConfiguration() throws Exception {
        return new MongoDbConfiguration(mongoDbConnectionUri, "mydb", "messages");
    }
}
