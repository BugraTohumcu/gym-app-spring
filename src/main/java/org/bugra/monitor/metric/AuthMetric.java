package org.bugra.monitor.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AuthMetric {

    private final Counter loginCounter;
    private final Counter changePasswordCounter;

    public AuthMetric(MeterRegistry meterRegistry){
        this.loginCounter = Counter.builder("gymapp.auth.login.total")
                .tags("environment", "development")
                .description("Total login attempts")
                .register(meterRegistry);

        this.changePasswordCounter = Counter.builder("auth.change-password.total")
                .tags("environment", "development")
                .description("Total password change attempts")
                .register(meterRegistry);
    }

    public void incrementLoginCounter(){
        this.loginCounter.increment();
    }


    public void incrementChangePasswordCounter(){
        this.changePasswordCounter.increment();
    }
}
