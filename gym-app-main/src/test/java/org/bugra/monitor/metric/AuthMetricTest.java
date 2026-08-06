package org.bugra.monitor.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.*;

class AuthMetricTest {

    private MeterRegistry meterRegistry;

    private AuthMetric authMetric;

    @BeforeEach
    void setup(){
        this.meterRegistry = new SimpleMeterRegistry();

        this.authMetric = new AuthMetric(meterRegistry);
    }

    @Test
    @DisplayName("Should increment total login by one")
    void incrementLoginCounter() {
        Counter counter = meterRegistry.find("gymapp.auth.login.total").counter();

        double current = counter.count();
        authMetric.incrementLoginCounter();
        double incremented = counter.count();

        assertEquals(1, incremented - current);
    }

    @Test
    @DisplayName("Should increment total change-password by one")
    void incrementChangePasswordCounter() {
        Counter counter = meterRegistry.find("gymapp.auth.change-password.total").counter();

        double current = counter.count();
        authMetric.incrementChangePasswordCounter();
        double incremented = counter.count();

        assertEquals(1, incremented - current);
    }
}