package server.dw.health;

import org.springframework.context.ConfigurableApplicationContext;

import com.codahale.metrics.health.HealthCheck;

public class SpringContextCheck extends HealthCheck {
 
    private ConfigurableApplicationContext ctx;
 
    public SpringContextCheck(ConfigurableApplicationContext ctx) {
        this.ctx = ctx;
    }
 
    @Override
    protected Result check() throws Exception {
        if(ctx.isActive() && ctx.isRunning()) {
            return Result.healthy("Spring context " + ctx.getDisplayName() + " is active and running!");
        }
        return Result.unhealthy("Spring context " + ctx.getDisplayName() + " is either innactive or not running");
    }
 
}