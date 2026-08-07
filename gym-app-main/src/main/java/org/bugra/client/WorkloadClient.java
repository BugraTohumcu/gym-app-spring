package org.bugra.client;

import org.bugra.config.WorkloadServiceFeignConfig;
import org.bugra.dto.client.SaveTrainerWorkload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "workload-service", configuration = WorkloadServiceFeignConfig.class)
public interface WorkloadClient {

    @PostMapping("/workload")
    void saveTrainerWorkload(SaveTrainerWorkload saveTrainerWorkload);
}
