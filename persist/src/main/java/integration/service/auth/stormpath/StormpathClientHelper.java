package integration.service.auth.stormpath;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.application.ApplicationCriteria;
import com.stormpath.sdk.application.ApplicationList;
import com.stormpath.sdk.application.Applications;
import com.stormpath.sdk.client.Client;

/**
 * The Stormpath client is a bit difficult to mock at times so this class acts as something of a wrapper around the
 * Stormpath {@link Client} to make unit testing easier. Additionally, it is intended to simplify the {@link Client} API
 * in places, such as providing a way to access an application by name under the assumption that the application name is
 * unique.
 */
@Component
public class StormpathClientHelper {

    private final StormpathClientFactory clientFactory;

    private final String applicationName;

    private Client client;

    private Application application;

    @Inject
    StormpathClientHelper(StormpathClientFactory factory, @Value("${application.name}") String applicationName) {
        this.clientFactory = factory;
        this.applicationName = applicationName;
    }

    @PostConstruct
    void init() {
        this.client = clientFactory.instance();
        this.application = fetchApplicationFromStormpath(applicationName);
    }

    Application fetchApplicationFromStormpath(String applicationName) {
        // Construct a Stormpath query to locate the application by name
        ApplicationCriteria query = Applications.where(Applications.name().eqIgnoreCase(applicationName));
        ApplicationList applications = client.getApplications(query);
        return applications.iterator().next();
    }

    String getApplicationName() {
        return applicationName;
    }

    Application getApplication() {
        return application;
    }

    Client getClient() {
        return client;
    }
}
