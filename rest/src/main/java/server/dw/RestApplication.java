package server.dw;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import server.dw.health.SpringContextCheck;
import server.dw.managed.SpringApplicationContextManager;
import server.dw.resource.MessageResource;
import core.business.MessageService;
import core.config.AppConfiguration;

public class RestApplication extends Application<RestApplicationConfiguration> {

    public static void main(String[] args) throws Exception {
        new RestApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<RestApplicationConfiguration> bootstrap) {
    }

    @Override
    public void run(RestApplicationConfiguration configuration, Environment environment) throws Exception {

        // Start up a Spring context
        ConfigurableApplicationContext rootContext = createSpringRootContext(configuration);
        environment.lifecycle().manage(new SpringApplicationContextManager(rootContext));
        environment.healthChecks().register("Spring Root Context", new SpringContextCheck(rootContext));

        AnnotationConfigApplicationContext ctx = createSpringContext(environment.getName(), rootContext);
        environment.healthChecks().register("Spring Application Context", new SpringContextCheck(ctx));

        // Create a message resource and register it
        MessageService service = ctx.getBean(MessageService.class);
        environment.jersey().register(new MessageResource(service));
    }

    /**
     * <p>
     * Creates the "root" Spring context.
     * 
     * <p>
     * See this: http://n-chandra.blogspot.fr/2014/04/the-sweetness-of-developing-rest.html
     * <br/>
     * And this: http://docs.spring.io/autorepo/docs/spring/3.1.x/javadoc-api/org/springframework/web/WebApplicationInitializer.html
     * 
     * @param configuration
     * @return
     */
    private ConfigurableApplicationContext createSpringRootContext(RestApplicationConfiguration configuration) {
        // Create the 'root' Spring application context
        ConfigurableApplicationContext rootContext = new GenericApplicationContext();
        rootContext.refresh();
        rootContext.getBeanFactory().registerSingleton("configuration", configuration);
        rootContext.registerShutdownHook();
        rootContext.start();
        return rootContext;
    }

    private AnnotationConfigApplicationContext createSpringContext(String contextName,
            ConfigurableApplicationContext parent) {

        // the real main app context has a link to the parent context
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.setParent(parent);
        ctx.register(AppConfiguration.class);
        ctx.setDisplayName("AnnotationConfigApplicationContext for " + contextName);
        ctx.refresh();
        ctx.registerShutdownHook();
        ctx.start();

        return ctx;
    }

}
