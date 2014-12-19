package integration.service.auth.stormpath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.api.ApiKeys;
import com.stormpath.sdk.cache.Caches;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.ClientBuilder;
import com.stormpath.sdk.client.Clients;

/**
 * A factory for a {@link Client}. There should only be one {@link StormpathClientFactory} per application and there
 * will be only one {@link Client} per factory.
 */
@Component
public class StormpathClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(StormpathClientFactory.class);

    private static String path;

    private static boolean disableCache;

    private static class StormPathClientHolder {
        private static final Logger logger = LoggerFactory.getLogger(StormpathAuthenticationService.class);
        private static final Client INSTANCE = createStormpathClient(path, disableCache);

        private static Client createStormpathClient(String apiKeyPath, boolean disableCache) {
            try {
                ApiKey apiKey = ApiKeys.builder().setFileLocation(apiKeyPath).build();

                ClientBuilder clientBuilder = Clients.builder();
                clientBuilder.setApiKey(apiKey);
                if (disableCache) {
                    clientBuilder.setCacheManager(Caches.newDisabledCacheManager());
                    logger.warn("Disabling Stormpath client cache which will be inefficient in a production environment");
                }
                // It is possible to switch off Stormpath's custom authentication mechanism like so:
                // clientBuilder.setAuthenticationScheme(AuthenticationScheme.BASIC);
                Client client = clientBuilder.build();
                logger.debug("Created Stormpath client instance \"{}\" with key \"{}\" associated with tenant: \"{}\"",
                        new Object[] { client.hashCode(), client.getApiKey(), client.getCurrentTenant().getName() });
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
        
        logger.debug("Returning Stormpath client instance \"{}\" associated with tenant: \"{}\"", client.hashCode(),
                client.getCurrentTenant().getName());

        return client;
    }

    // Below, injection with setters is a workaround for the fact that Spring won't inject into static fields.

    /**
     * Sets the location of the API key file that contains the key and the secret.
     * 
     * @param apiKeyPath
     */
    @Value("${STORMPATH_API_KEY_PATH}")
    public void setStormPathApiKeyPath(String apiKeyPath) {
        StormpathClientFactory.path = apiKeyPath;
    }

    /**
     * While production applications will usually enable a working CacheManager as described above, you might wish to
     * disable caching entirely when testing or debugging to remove ‘moving parts’ for better clarity into
     * request/response behavior. Set to true in development environments.
     * 
     * @param disableCache
     */
    @Value("${DISABLE_STORMPATH_CLIENT_CACHE}")
    public void setDisableCache(boolean disableCache) {
        StormpathClientFactory.disableCache = disableCache;
    }
}
