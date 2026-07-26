package org.bugra.monitor.metric;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bugra.enums.UserRole;
import org.bugra.persistence.repo.UserRepo;
import org.springframework.stereotype.Component;

/**
 * <p>This class is responsible for providing the following metrics</p>
 * <ul>
 *     <li>Total number of users in system</li>
 *     <li>Total number of active users in the system</li>
 *     <li>Total number of passive users in the system</li>
 *     <li>Total number of trainees</li>
 *     <li>Total number of trainers</li>
 * </ul>
 * */

@Component
public class UserMetric {

    public UserMetric(MeterRegistry meterRegistry, UserRepo userRepo){

        // Total number of users
        Gauge.builder("gymapp.user.total", userRepo, UserRepo::findUserCount)
                .description("Total number of users")
                .tag("environment","development")
                .register(meterRegistry);

        // Only active users
        Gauge.builder("gymapp.user.total.status", userRepo, repo -> repo.findUserCountByStatus(true))
                .description("Total number of active users")
                .tag("environment","development")
                .tag("status","active")
                .register(meterRegistry);

        // Only active passive
        Gauge.builder("gymapp.user.total.status", userRepo, repo -> repo.findUserCountByStatus(false))
                .description("Total number of passive users")
                .tag("environment","development")
                .tag("status","passive")
                .register(meterRegistry);

        // Only trainees
        Gauge.builder("gymapp.user.total.role", userRepo, repo -> repo.findUserCountByRole(UserRole.TRAINEE))
                .description("Total number of trainees")
                .tag("environment","development")
                .tag("role","trainee")
                .register(meterRegistry);


        // Only trainers
        Gauge.builder("gymapp.user.total.role", userRepo, repo -> repo.findUserCountByRole(UserRole.TRAINER))
                .description("Total number of trainers")
                .tag("environment","development")
                .tag("role","trainer")
                .register(meterRegistry);
    }
}
