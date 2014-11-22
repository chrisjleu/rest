package server.dw.jee.filter;

import io.dropwizard.jetty.HttpConnectorFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.ForwardedRequestCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * Ensures that non-secure HTTP requests are redirected to their secure equivalent. This includes any requests that are
 * The need for this can arise in environments where SSL termination occurs before the request reaches the application
 * server. See RFC 7239 (http://tools.ietf.org/html/rfc7239) for more information about the Forwarded HTTP Extension.
 * </p>
 * 
 * <p>
 * However, the <code>useForwardedHeaders</code> configuration variable of {@link HttpConnectorFactory} dictates whether
 * the <code>X-Forwarded</code> headers are taken into consideration when determining whether a request is secure or
 * not.
 * </p>
 * 
 * <p>
 * You might simulate a request like so:
 * 
 * {@code curl -L -X POST -H "Content-Type: application/json" -H "x-forwarded-proto: https" http://localhost:8080}
 * 
 * <p>
 * <ul>
 * <li>{@code -L} instructs curl to follow redirects.</li>
 * <li>{@code -H} sets headers (of which you can specify multiple). Pay attention to the x-forwarded-proto header.</li>
 * </ul>
 * </p>
 * 
 * 
 * @see HttpConnectorFactory
 * @see ForwardedRequestCustomizer
 * 
 */
public class HttpsEnforcer implements Filter {

    private static Logger logger = LoggerFactory.getLogger(HttpsEnforcer.class);

    private static final String X_FORWARDED_PROTO = "x-forwarded-proto";

    private boolean isEnabled = true;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String enabledFlag = filterConfig.getInitParameter("enabled");
        if (enabledFlag != null) {
            isEnabled = Boolean.parseBoolean(enabledFlag);
        }

        if (isEnabled) {
            logger.info("Servlet filter \"{}\" initialized and enabled", filterConfig.getFilterName());
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (isEnabled) {
            if (!request.isSecure()) {
                if (!isOriginalRequestSecure(request.getHeader(X_FORWARDED_PROTO))) {
                    // X-Forwared header does not indicate that the original request was secure
                    // Therefore proceed with the redirect to the secure euivalent
                    String secureUrl = constructSecureUrl(request);
                    logger.debug("Redirecting non-secure request to \"{}\"", secureUrl);
                    response.sendRedirect(secureUrl.toString());
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    boolean isOriginalRequestSecure(String forwardedProtocol) {
        boolean isSecure = false;

        if (forwardedProtocol != null) {
            if (forwardedProtocol.trim().indexOf("https") == 0) {
                // Original request was secure
                isSecure = true;
            }
        }
        return isSecure;
    }

    String constructSecureUrl(HttpServletRequest request) throws MalformedURLException {
        URL secureUrl = new URL("https", request.getServerName(), request.getLocalPort(), request.getPathInfo());
        StringBuilder secureUrlBuilder = new StringBuilder(secureUrl.toString());
        secureUrlBuilder.append("?").append(request.getQueryString());
        return secureUrlBuilder.toString();
    }

    @Override
    public void destroy() {
        // nothing
    }
}
