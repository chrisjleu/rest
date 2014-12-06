package server.dw.jee.filter;

import io.dropwizard.jetty.HttpConnectorFactory;

import java.io.IOException;
import java.net.MalformedURLException;

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
 * SSL terminated and contain the appropriate X-Forward headers. See RFC 7239 (http://tools.ietf.org/html/rfc7239) for
 * more information about the Forwarded HTTP Extension.
 * </p>
 * 
 * <p>
 * If using DropWizard, the <code>useForwardedHeaders</code> configuration variable of {@link HttpConnectorFactory}
 * dictates whether the <code>X-Forwarded</code> headers are taken into consideration when determining whether a request
 * is secure or not. If this is set to true then the additional headers will be observed and the <code>isSecure</code>
 * method of {@link HttpServletRequest} will behave as expected.
 * </p>
 * <p>
 * Note also that a 301 status code (moved permanently) is set (rather than the default 302 that is used by default with
 * the <code>sendRedirect</code> method, although some existing HTTP/1.0 user agents will erroneously change a POST
 * request into a GET as noted in the RFC 2616 section 10 (http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html).
 * </p>
 * <p>
 * Simulate requests like so:
 * 
 * <pre>
 * Http request with x-forwarded-proto header:
 * 
 * {@code curl -L -X POST -H "Content-Type: application/json" -H "x-forwarded-proto: https" http://localhost:8080}
 * 
 * HTTP request no x-forwarded header
 * 
 * {@code curl -L -X POST -H "Content-Type: application/json" http://localhost:8080}
 * 
 * HTTPS request no x-forwarded header
 * 
 * {@code curl -L -X POST -H "Content-Type: application/json" https://localhost:8080}
 * </pre>
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
                    // X-Forwarded header does not indicate that the original request was secure
                    // Therefore proceed with the redirect to the secure equivalent
                    String location = constructSecureUrl(request);
                    response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                    response.setHeader("Location", location);
                    logger.info("Redirecting to {}", location);
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
        String originalRequestUrl = getFullURL(request);
        String secureUrl = "https://" + originalRequestUrl.substring("http://".length());
        logger.debug("Constructed \"{}\" from non-secure \"{}\"", secureUrl, originalRequestUrl);
        return secureUrl;
    }

    String getFullURL(HttpServletRequest request) {
        final StringBuilder requestURL = new StringBuilder(100).append(request.getRequestURL());
        String queryString = request.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    @Override
    public void destroy() {
        // nothing
    }
}
