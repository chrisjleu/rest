package server.dw.jee.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SSLTerminationChecker implements Filter {

    private static Logger logger = LoggerFactory.getLogger(SSLTerminationChecker.class);

    private static final String X_FORWARDED_PROTO = "x-forwarded-proto";

    private boolean isEnabled = true;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String enabledFlag = filterConfig.getInitParameter("enabled");
        if (enabledFlag != null) {
            isEnabled = Boolean.parseBoolean(enabledFlag);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        if (isEnabled) {
            String xForwardProtoHttpHeader = servletRequest.getHeader(X_FORWARDED_PROTO);
            boolean isSecure = servletRequest.isSecure();
            if ("https".equalsIgnoreCase(xForwardProtoHttpHeader) && !isSecure) {
                logger.warn(
                        "{} header is {} but the isSecure() method returns false. This indicates that the original request was sent securely (HTTPS) but was terminated before in reached this server and the server is not taking into account the header.",
                        X_FORWARDED_PROTO, xForwardProtoHttpHeader);
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
