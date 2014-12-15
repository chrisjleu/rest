package server.dw;

import io.dropwizard.Application;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicAuthProvider;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import server.dw.auth.OAuth2Provider;
import server.dw.auth.UserBasicAuthAuthenticator;
import server.dw.auth.UserOauth2Authenticator;
import server.dw.config.env.EnvironmentVariableInterpolationBundle;
import server.dw.jee.servlet.CacheFlushStatsServlet;
import server.dw.resource.AuthenticationResource;
import server.dw.resource.ErrorMessageBodyWriter;
import server.dw.resource.MessageResource;
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

        // **************************************** //
        // ************ Authenticators ************ //
        // **************************************** //
        // A number of dependencies must be obtained in order to build the Authenticator
        UserBasicAuthAuthenticator userBasicAuthAuthenticator = new UserBasicAuthAuthenticator(userService);
        CacheBuilderSpec cachePolicy = configuration.getAuthenticationCachePolicy().buildPolicy();

        // Create an authenticator to provide basic authentication
        CachingAuthenticator<BasicCredentials, User> cachingAuthenticator = new CachingAuthenticator<BasicCredentials, User>(
                environment.metrics(), userBasicAuthAuthenticator, cachePolicy);
        BasicAuthProvider<User> basicAuthProvider = new BasicAuthProvider<User>(cachingAuthenticator,
                environment.getName());

        // Finally, register the authentication provider
        // environment.jersey().register(basicAuthProvider);

        // This registers an Oauth2 provider
        environment.jersey().register(
                new OAuth2Provider<User>(new UserOauth2Authenticator(userService), environment.getName()));
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

        // TASKS:
        environment.admin().addTask(new ClearCachingAuthenticatorTask(cachingAuthenticator));

        // CUSTOM SERVLETS
        ServletRegistration.Dynamic servlet = environment.admin().addServlet("CacheFlushStatsServlet",
                new CacheFlushStatsServlet(cachingAuthenticator));
        servlet.addMapping("/cache");
    }

}
