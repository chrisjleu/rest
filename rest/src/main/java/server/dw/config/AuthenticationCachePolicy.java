package server.dw.config;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.CacheBuilderSpec;

public class AuthenticationCachePolicy {

    private static final String CACHE_SPECIFICATION = "maximumSize=%s, expireAfterAccess=%s";

    @Min(0)
    @Max(10000)
    private int maxCacheSize;

    @NotBlank
    private String cacheExpiryTime;

    /**
     * Constructs the policy from the fields of this class.
     * 
     * @return A {@link CacheBuilderSpec}.
     */
    public CacheBuilderSpec buildPolicy() {
        String cacheSpec = String.format(CACHE_SPECIFICATION, maxCacheSize, cacheExpiryTime);
        return CacheBuilderSpec.parse(cacheSpec);
    }

    // /////////// GETTERS AND SETTERS ////////////////

    @JsonProperty("maxCacheSize")
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    @JsonProperty("maxCacheSize")
    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }

    @JsonProperty("cacheExpiryTime")
    public String getCacheExpiryTime() {
        return cacheExpiryTime;
    }

    @JsonProperty("cacheExpiryTime")
    public void setCacheExpiryTime(String cacheExpiryTime) {
        this.cacheExpiryTime = cacheExpiryTime;
    }
}
