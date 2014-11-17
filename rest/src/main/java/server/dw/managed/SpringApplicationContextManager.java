package server.dw.managed;

import io.dropwizard.lifecycle.Managed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringApplicationContextManager implements Managed {

    Logger logger = LoggerFactory.getLogger(SpringApplicationContextManager.class);
    
    private final ConfigurableApplicationContext context;

    public SpringApplicationContextManager(ConfigurableApplicationContext ctx) {
        this.context = ctx;
    }

    @Override
    public void start() throws Exception {
        // Nothing needed to start the client
    }

    @Override
    public void stop() throws Exception {
        logger.info("Shutting down Spring context \"{}\"", context.getDisplayName());
        context.close();
    }

}
