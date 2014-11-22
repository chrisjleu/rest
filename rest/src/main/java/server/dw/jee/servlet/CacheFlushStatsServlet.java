package server.dw.jee.servlet;

import io.dropwizard.auth.CachingAuthenticator;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import com.google.common.cache.CacheStats;

public class CacheFlushStatsServlet extends HttpServlet {

    /**
     * Generated.
     */
    private static final long serialVersionUID = -1160838093207744032L;

    private static final String CONTENT_TYPE = MediaType.TEXT_HTML;

    private transient final CachingAuthenticator<?, ?> cachingAuthenticator;

    public CacheFlushStatsServlet(CachingAuthenticator<?, ?> cachingAuthenticator) {
        this.cachingAuthenticator = cachingAuthenticator;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cachingAuthenticator.invalidateAll();
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final CacheStats stats = cachingAuthenticator.stats();
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        resp.setContentType(CONTENT_TYPE);
        final PrintWriter writer = resp.getWriter();
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("cacheSize=").append(cachingAuthenticator.size()).append(", ");
            builder.append(stats);
            writer.println(builder.toString());
        } finally {
            writer.close();
        }
    }
}
