package core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = { "core" })
@Import(integration.config.AppConfiguration.class)
public class AppConfiguration {
}
