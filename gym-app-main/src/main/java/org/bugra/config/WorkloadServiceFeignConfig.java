package org.bugra.config;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WorkloadServiceFeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            String transactionId = MDC.get("transactionId");
            if(transactionId != null){
                requestTemplate.header("transactionId", transactionId);
            }

            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if(requestAttributes != null){
                String header = requestAttributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

                if(header != null && header.startsWith("Bearer ")){
                    String accessToken = header.substring(7);
                    requestTemplate.header(HttpHeaders.AUTHORIZATION, accessToken);
                }
            }
        };
    }
}
