package server.dw.task;

import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import io.dropwizard.servlets.tasks.Task;

import java.io.PrintWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import api.representations.TokenResponse;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMultimap;

public class ClearCachingAuthenticatorTask extends Task {

    private Logger logger = LoggerFactory.getLogger(ClearCachingAuthenticatorTask.class);
    
    final CachingAuthenticator<BasicCredentials, TokenResponse> cachedAuthenticator;
    
    public ClearCachingAuthenticatorTask(CachingAuthenticator<BasicCredentials, TokenResponse> cachedAuthenticator) {
        super("InvalidateAuthenticationCache");
        this.cachedAuthenticator = cachedAuthenticator;
    }

    @Override
    @Timed
    public void execute(ImmutableMultimap<String, String> arg0, PrintWriter arg1) throws Exception {
        logger.info("Clearing authentication cache of {} elements ", cachedAuthenticator.size());
        this.cachedAuthenticator.invalidateAll();
    }

}
