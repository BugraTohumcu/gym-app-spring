package org.bugra.monitor.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.bugra.enums.UserRole;
import org.bugra.persistence.repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMetricTest {

    @Mock
    private UserRepo userRepo;

    private SimpleMeterRegistry meterRegistry;
    private UserMetric userMetric;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();

        userMetric = new UserMetric(meterRegistry, userRepo);
    }

    @Test
    @DisplayName("Should register gymapp.users.total gauge and fetch count")
    void testTotalUsersGauge() {
        when(userRepo.findUserCount()).thenReturn(100L);

        Gauge gauge = meterRegistry.find("gymapp.user.total").gauge();

        assertNotNull(gauge, "Gauge should be registered");
        assertEquals(100.0, gauge.value(), "Gauge should return the value from repository");
    }

    @Test
    @DisplayName("Should register active users gauge and fetch count")
    void testActiveUsersGauge() {
        when(userRepo.findUserCountByStatus(true)).thenReturn(45L);

        Gauge gauge = meterRegistry.find("gymapp.user.total.status")
                .tag("status", "active")
                .gauge();

        assertNotNull(gauge);
        assertEquals(45.0, gauge.value());
    }

    @Test
    @DisplayName("Should register trainee role gauge and fetch count")
    void testTraineeRoleGauge() {
        when(userRepo.findUserCountByRole(UserRole.TRAINEE)).thenReturn(30L);

        Gauge gauge = meterRegistry.find("gymapp.user.total.role")
                .tag("role", "trainee")
                .gauge();

        assertNotNull(gauge);
        assertEquals(30.0, gauge.value());
    }
}