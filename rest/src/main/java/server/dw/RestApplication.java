package server.dw;

import io.dropwizard.Application;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import server.dw.auth.UserAuthenticator;
import server.dw.health.SpringContextCheck;
import server.dw.managed.SpringApplicationContextManager;
import server.dw.resource.AuthenticationResource;
import server.dw.resource.ErrorMessageBodyWriter;
import server.dw.resource.MessageResource;
import api.representations.User;

import com.codahale.metrics.MetricRegistry;
import com.google.common.cache.CacheBuilderSpec;

import core.AppConfiguration;
import core.service.MessageService;
import core.service.UserService;

/**
 * <p>
 * This class has the <code>main</code> method that starts the application. Start it like so:
 * 
 * <pre>
 * java -jar rest/target/mrestserver.jar server rest/server.yaml
 * </pre>
 * 
 * </p>
 * <p>
 * Examine the file <code>server.yaml</code> in the root folder for the port the application is listening on.
 * </p>
 */
public class RestApplication extends Application<RestApplicationConfiguration> {

    private Logger logger = LoggerFactory.getLogger(RestApplication.class);

    public static void main(String[] args) throws Exception {
        new RestApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<RestApplicationConfiguration> bootstrap) {
    }

    @Override
    public void run(RestApplicationConfiguration configuration, Environment environment) throws Exception {

        // Start up a Spring context. This will be the provider of objects from the core module (rather than
        // instantiating them "manually" in this class)
        ConfigurableApplicationContext rootContext = createSpringRootContext(configuration);
        environment.lifecycle().manage(new SpringApplicationContextManager(rootContext));
        environment.healthChecks().register("Spring Root Context", new SpringContextCheck(rootContext));

        AnnotationConfigApplicationContext ctx = createSpringContext(environment.getName(), rootContext);
        environment.healthChecks().register("Spring Application Context", new SpringContextCheck(ctx));

        // ******************************************** //
        // ************ Custom Serializers ************ //
        // ******************************************** //
        environment.jersey().register(new ErrorMessageBodyWriter());

        // *********************************** //
        // ************ Resources ************ //
        // *********************************** //
        // Message resource
        MessageService messageService = ctx.getBean(MessageService.class);
        environment.jersey().register(new MessageResource(messageService));

        // User login/registration resource
        environment.jersey().register(new AuthenticationResource());

        // **************************************** //
        // ************ Authenticators ************ //
        // **************************************** //
        // A number of dependencies must be obtained in order to build the Authenticator
        UserService userService = ctx.getBean(UserService.class);
        UserAuthenticator dbAuthenticator = new UserAuthenticator(userService);
        MetricRegistry metricRegistry = new MetricRegistry();
        CacheBuilderSpec cachePolicy = configuration.getAuthenticationCachePolicy().buildPolicy();

        // Create an authenticator to provide basic authentication
        CachingAuthenticator<BasicCredentials, User> cachedAuthenticator = new CachingAuthenticator<BasicCredentials, User>(
                metricRegistry, dbAuthenticator, cachePolicy);
        BasicAuthProvider<User> basicAuthProvider = new BasicAuthProvider<User>(cachedAuthenticator,
                environment.getName());

        // Finally, register the authentication provider
        logger.info("Registering basic authentication provider with cache policy {}", cachePolicy);
        environment.jersey().register(basicAuthProvider);

    }

    /**
     * <p>
     * Creates the "root" Spring context.
     * 
     * <p>
     * See <a href="http://n-chandra.blogspot.fr/2014/04/the-sweetness-of-developing-rest.html">this</a> and <a href=
     * "http://docs.spring.io/autorepo/docs/spring/3.1.x/javadoc-api/org/springframework/web/WebApplicationInitializer.html"
     * >this</a> for more information on registering Spring with DropWizard.
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
