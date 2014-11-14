package core;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = { "core.service" })
@Import(integration.config.AppConfiguration.class)
public class AppConfiguration {
}
