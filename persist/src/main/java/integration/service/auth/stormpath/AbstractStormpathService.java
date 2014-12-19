package integration.service.auth.stormpath;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.Client;

public abstract class AbstractStormpathService {

    private final Logger logger = LoggerFactory.getLogger(AbstractStormpathService.class);

    private final StormpathClientFactory clientFactory;

    private final String applicationName;

    private Client client;

    private Application application;

    AbstractStormpathService(StormpathClientFactory factory, String applicationName) {
        this.clientFactory = factory;
        this.applicationName = applicationName;
    }

    @PostConstruct
    void init() {
        this.client = clientFactory.instance();

        // Locate the application by name
        ApplicationCriteria query = Applications.where(Applications.name().eqIgnoreCase(applicationName));
        ApplicationList applications = client.getApplications(query);
        application = applications.iterator().next();
        
        logger.info("Stormpath application for {} is \"{}\"", this.getClass().getName(), application.getName());
    }

    protected Application getApplication() {
        return application;
    }
    
    public Client getClient() {
        return client;
    }
}
