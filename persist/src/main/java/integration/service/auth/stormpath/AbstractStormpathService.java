package integration.service.auth.stormpath;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.Client;

public abstract class AbstractStormpathService {

    private final StormpathClientFactory clientFactory;

    private final String applicationName;

    private Client client;

    private Application application;

    @Inject
    AbstractStormpathService(StormpathClientFactory factory, @Value("${application.name}") String applicationName) {
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
    }

    protected Application getApplication() {
        return application;
    }
    
    public Client getClient() {
        return client;
    }
}
