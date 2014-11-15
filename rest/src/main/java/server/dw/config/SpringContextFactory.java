package server.dw.config;

import io.dropwizard.setup.Environment;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import server.dw.RestApplicationConfiguration;
import server.dw.health.SpringContextCheck;
import server.dw.managed.SpringApplicationContextManager;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SpringContextFactory {

    private Logger logger = LoggerFactory.getLogger(SpringContextFactory.class);

    private String parentContextDisplayName = GenericApplicationContext.class.getName();

    private String displayName = AnnotationConfigApplicationContext.class.getName();

    @NotEmpty
    private String applicationConfigurationClass;

    /**
     * Builds the Spring parent and child context.
     * 
     * @param configuration
     * @param environment
     * @return The child Spring context.
     * @throws ClassNotFoundException
     */
    public ConfigurableApplicationContext build(RestApplicationConfiguration configuration, Environment environment)
            throws ClassNotFoundException {
        ConfigurableApplicationContext rootContext = createSpringRootContext(configuration);
        environment.lifecycle().manage(new SpringApplicationContextManager(rootContext));
        environment.healthChecks().register("Spring root application context", new SpringContextCheck(rootContext));

        ConfigurableApplicationContext context = createSpringContext(rootContext);
        environment.healthChecks().register("Spring application context", new SpringContextCheck(context));

        logger.info("Created and started Spring context \"{}\" ({}) with parent \"{}\" ({})", context.getDisplayName(),
                context.getClass().getName(), rootContext.getDisplayName(), rootContext.getClass().getName());

        return context;
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
        GenericApplicationContext rootContext = new GenericApplicationContext();
        rootContext.setDisplayName(parentContextDisplayName);
        rootContext.refresh();
        rootContext.getBeanFactory().registerSingleton("configuration", configuration);
        rootContext.registerShutdownHook();
        rootContext.start();
        return rootContext;
    }

    private GenericApplicationContext createSpringContext(ConfigurableApplicationContext parent)
            throws ClassNotFoundException {

        // the real main app context has a link to the parent context
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.setParent(parent);
        ctx.register(Class.forName(applicationConfigurationClass)); // Should be the fully qualified class name
        ctx.setDisplayName(displayName);
        ctx.refresh();
        ctx.registerShutdownHook();
        ctx.start();

        return ctx;
    }

    @JsonProperty
    public String getParentContextDisplayName() {
        return parentContextDisplayName;
    }

    @JsonProperty
    public void setParentContextDisplayName(String parentContextDisplayName) {
        this.parentContextDisplayName = parentContextDisplayName;
    }

    @JsonProperty
    public String getDisplayName() {
        return displayName;
    }

    @JsonProperty
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @JsonProperty
    public String getApplicationConfigurationClass() {
        return applicationConfigurationClass;
    }

    @JsonProperty
    public void setApplicationConfigurationClass(String applicationConfigurationClass) {
        this.applicationConfigurationClass = applicationConfigurationClass;
    }
}
