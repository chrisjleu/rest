package integration.service.auth.stormpath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.Clients;

/**
 * A factory for a {@link Client}. There should only be one {@link StormpathClientFactory} per application and there
 * will be only one {@link Client} per factory.
 */
@Component
public class StormpathClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(StormpathClientFactory.class);

    @Value("${STORMPATH_API_KEY_PATH}")
    private static String path;

    private static class StormPathClientHolder {
        private static final Logger logger = LoggerFactory.getLogger(StormpathAuthenticationService.class);
        private static final Client INSTANCE = createStormpathClient(path);

        private static Client createStormpathClient(String apiKeyPath) {
            try {
                ApiKey apiKey = ApiKeys.builder().setFileLocation(apiKeyPath).build();
                Client client = Clients.builder().setApiKey(apiKey).build();
                logger.info("Created Stormpath client \"{}\"", client.hashCode());
                return client;
            } catch (final Exception e) {
                throw new Error(e);
            }
        }
    }

    /**
     * Gets the instance of the {@link Client} - there is only one supposed to be one of these.
     * 
     * @return The {@link Client} instance of which there is only one per {@link StormpathClientFactory}.
     * @throws Exception
     *             Thrown if the client could not establish a connection to the database.
     */
    Client instance() {
        Client client = StormPathClientHolder.INSTANCE;
        if (logger.isDebugEnabled()) {
            logger.debug("Returning client instance \"{}\" associated with key: \"{}\"", client.hashCode(),
                    client.getApiKey());
        }
        return client;
    }

}
