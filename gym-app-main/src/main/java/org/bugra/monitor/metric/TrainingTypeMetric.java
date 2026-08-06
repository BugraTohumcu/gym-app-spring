package org.bugra.monitor.metric;


import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.bugra.persistence.repo.TrainingTypeRepo;
import org.springframework.stereotype.Component;


/**
 * <p>This class is responsible for providing the following metrics</p>
 * <ul>
 *     <li>Total number of training types</li>
 * </ul>
 * */
@Component
public class TrainingTypeMetric {

    public TrainingTypeMetric(MeterRegistry meterRegistry, TrainingTypeRepo trainingTypeRepo){

        Gauge.builder("gymapp.training.types.total", trainingTypeRepo, repo -> repo.findAllTypes().size())
                .description("Total number of training types currently in the database")
                .tags("environment", "development")
                .register(meterRegistry);
    }

}