package server.dw;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.ChainedAuthFactory;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import server.dw.auth.BasicAuthAuthenticator;
import server.dw.auth.OAuth2Factory;
import server.dw.auth.Oauth2Authenticator;
import server.dw.config.env.EnvironmentVariableInterpolationBundle;
import server.dw.jee.servlet.CacheFlushStatsServlet;
import server.dw.resource.AuthenticationResource;
import server.dw.resource.ErrorMessageBodyWriter;
import server.dw.resource.MessageResource;
import server.dw.resource.OauthTokenResource;
import server.dw.task.ClearCachingAuthenticatorTask;
import api.representations.User;

import com.google.common.cache.CacheBuilderSpec;

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

    private static Logger logger = LoggerFactory.getLogger(RestApplication.class);

    public static void main(String[] args) throws Exception {
        new RestApplication().run(args);
    }

    @Override
    public String getName() {
        return "DropWizard Experiment";
    }

    @Override
    public void initialize(Bootstrap<RestApplicationConfiguration> bootstrap) {
        bootstrap.addBundle(new EnvironmentVariableInterpolationBundle());
    }

    @Override
    public void run(RestApplicationConfiguration configuration, Environment environment) throws Exception {
        logger.info("Running: {}", environment.getName());

        // Start up a Spring context. This will be the provider of objects from the core module (rather than
        // instantiating them "manually" in this class)
        ConfigurableApplicationContext ctx = configuration.getSpringContextFactory().build(configuration, environment);

        // *********************************************** //
        // ************ Set Jersey properties ************ //
        // *********************************************** //
        // environment.jersey().property("jersey.config.beanValidation.enableOutputValidationErrorEntity.server",
        // false);

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
        UserService userService = ctx.getBean(UserService.class);
        environment.jersey().register(new AuthenticationResource(userService));

        // Token resource for API access
        environment.jersey().register(new OauthTokenResource());

        // **************************************** //
        // ************ Authenticators ************ //
        // **************************************** //
        // Basic Auth Authenticator
        BasicAuthAuthenticator basicAuthAuthenticator = new BasicAuthAuthenticator(userService);
        CacheBuilderSpec cachePolicy = configuration.getAuthenticationCachePolicy().buildPolicy();
        CachingAuthenticator<BasicCredentials, User> cachingAuthenticator = new CachingAuthenticator<BasicCredentials, User>(
                environment.metrics(), basicAuthAuthenticator, cachePolicy);
        BasicAuthFactory<User> basicAuthFactory = new BasicAuthFactory<User>(cachingAuthenticator,
                environment.getName(), User.class);

        // Oauth2 Authenticator
        Oauth2Authenticator userOauth2Authenticator = new Oauth2Authenticator(userService);
        OAuth2Factory<User> oAuthFactory = new OAuth2Factory<User>(userOauth2Authenticator, environment.getName(),
                User.class);

        environment.jersey().register(AuthFactory.binder(addFactoriesToChain(basicAuthFactory, oAuthFactory)));

        // ************************************* //
        // ************ JEE Filters ************ //
        // ************************************* //
        configuration.getFilterChainFactory().addAll(environment.servlets());

        // ************************************ //
        // ************ Admin page ************ //
        // ************************************ //
        // CUSTOM FILTERS
        configuration.admin().getFilterChainFactory().addAll(environment.admin());

        // LOGIN SECURITY
        environment.admin().setSecurityHandler(configuration.admin().getAdminSecurityProvider().instance());

        // DROPWIZARD TASKS:
        environment.admin().addTask(new ClearCachingAuthenticatorTask(cachingAuthenticator));

        // CUSTOM SERVLETS
        ServletRegistration.Dynamic servlet = environment.admin().addServlet("CacheFlushStatsServlet",
                new CacheFlushStatsServlet(cachingAuthenticator));
        servlet.addMapping("/cache");

    }

    @SuppressWarnings("unchecked")
    private ChainedAuthFactory<User> addFactoriesToChain(BasicAuthFactory<User> basicAuthFactory,
            OAuth2Factory<User> oAuthFactory) {
        return new ChainedAuthFactory<User>(basicAuthFactory, oAuthFactory);
    }

}
