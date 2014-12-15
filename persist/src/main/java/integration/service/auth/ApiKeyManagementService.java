package integration.service.auth;

import integration.api.model.apikey.ApiKey;

import java.util.List;

/**
 * Provides CRUD operations for API keys.
 */
public interface ApiKeyManagementService {

    ApiKey create();

    List<ApiKey> list();

    void disable();

    void enable();

    void delete();

}
