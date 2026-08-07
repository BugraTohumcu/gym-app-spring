package org.bugra.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "workload-service")
public interface WorkloadClient {

    @PostMapping("/workload")
    void saveTrainerWorkload();
}
