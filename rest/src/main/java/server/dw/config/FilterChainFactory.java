package server.dw.config;

import io.dropwizard.jetty.setup.ServletEnvironment;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

public class FilterChainFactory {

    private static Logger logger = LoggerFactory.getLogger(FilterChainFactory.class);
    
    private ImmutableList<FilterConfig> filterChain;

    @JsonProperty("filterChain")
    public ImmutableList<FilterConfig> getFilterChain() {
        return filterChain;
    }

    @JsonProperty("filterChain")
    public void setFilterChain(ImmutableList<FilterConfig> filterChain) {
        this.filterChain = filterChain;
    }

    /**
     * A convenience method to add all the configured filters to the environment.
     * 
     * @param environment
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public void addAll(ServletEnvironment servlets) throws ClassNotFoundException {
        for (FilterConfig filterConfig : filterChain) {
            Class<Filter> clazz = (Class<Filter>) Class.forName(filterConfig.getClassName());
            FilterRegistration.Dynamic filter = servlets.addFilter(filterConfig.getName(), clazz);
            filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, filterConfig.getPath());
            filter.setInitParameter("enabled", filterConfig.getEnabled());
            logger.info("Factory {} added filter with configuration: {}", this.hashCode(), filterConfig);
        }
    }

}
