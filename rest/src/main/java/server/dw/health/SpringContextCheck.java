package server.dw.health;

import org.springframework.context.ConfigurableApplicationContext;

import com.codahale.metrics.health.HealthCheck;

public class SpringContextCheck extends HealthCheck {
 
    private ConfigurableApplicationContext context;
 
    public SpringContextCheck(ConfigurableApplicationContext ctx) {
        this.context = ctx;
    }
 
    @Override
    protected Result check() throws Exception {
        if(context.isActive() && context.isRunning()) {
            return Result.healthy(context.getDisplayName() + " is active and running!");
        }
        return Result.unhealthy(context.getDisplayName() + " is either innactive or not running");
    }
 
}