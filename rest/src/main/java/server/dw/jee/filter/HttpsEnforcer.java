package server.dw.jee.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Ensures that non-secure HTTP requests are redirected to the secure equivalent if it turns out that the originating
 * request was sent securely. The need for this can arise in environments where SSL termination occurs before the
 * request reaches the application server. See RFC 7239 (http://tools.ietf.org/html/rfc7239) for more information about
 * the Forwarded HTTP Extension.
 * 
 * <p>
 * You might simulate a request like so:
 * 
 * {@code curl -L -X POST -H "Content-Type: application/json" -H "x-forwarded-proto: https" http://localhost:8080}
 * 
 * <p>
 * <ul>
 * <li>{@code -L} tells curl to follow redirects.</li>
 * <li>{@code -H} sets headers (of which you can specify multiple). Pay attention to the x-forwarded-proto header.</li>
 * <ul>
 * 
 */
public class HttpsEnforcer implements Filter {

    private static Logger logger = LoggerFactory.getLogger(HttpsEnforcer.class);

    private FilterConfig filterConfig;

    private static final String X_FORWARDED_PROTO = "x-forwarded-proto";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        logger.info("Initializing servlet filter \"{}\"", this.filterConfig.getFilterName());
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (!isOriginalRequestSecure(request.getHeader(X_FORWARDED_PROTO))) {
            String redirectPath = "https://" + request.getServerName() + request.getPathInfo();
            logger.debug("Redirecting non-secure original request to {}", redirectPath);
            response.sendRedirect(redirectPath);
            return;
        }

        filterChain.doFilter(request, response);
    }

    boolean isOriginalRequestSecure(String forwardedProtocol) {
        boolean isSecure = false;
        if (forwardedProtocol != null) {
            if (forwardedProtocol.trim().indexOf("https") != 0) {
                // Original request was NOT secure
                isSecure = true;
            }
        }
        return isSecure;
    }

    @Override
    public void destroy() {
        // nothing
    }
}
