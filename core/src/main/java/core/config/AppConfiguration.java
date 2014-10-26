package core.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import core.integration.MongoDbConfiguration;
import core.util.HostAndPort;

@Configuration
@ComponentScan(basePackages={"core"})
public class AppConfiguration {

    @Bean
    public MongoDbConfiguration mongoDbConfiguration() throws Exception {
        MongoDbConfiguration mongoDbConfiguration = new MongoDbConfiguration();
        mongoDbConfiguration.setCollName("messages");
        mongoDbConfiguration.setDbName("mydb");
        mongoDbConfiguration.setServerAddresses(Arrays.asList(mongoLocalhost()));
        
        return mongoDbConfiguration;
    }
    
    @Bean
    public HostAndPort mongoLocalhost() {
        return new HostAndPort("localhost", 27017);
    }

}
