package org.bugra.config;


import org.bugra.filter.AuthFilter;
import org.bugra.filter.RestLoggingFilter;
import org.bugra.filter.TransactionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<TransactionFilter> transactionFilterBean(){
        FilterRegistrationBean<TransactionFilter> transactionBean =
                new FilterRegistrationBean<>();

        transactionBean.setFilter(new TransactionFilter());
        transactionBean.setOrder(1);
        transactionBean.addUrlPatterns("/*");

        return transactionBean;
    }

    @Bean
    public FilterRegistrationBean<RestLoggingFilter> restLoggingFilterBean(){
        FilterRegistrationBean<RestLoggingFilter> restLoggingBean =
                new FilterRegistrationBean<>();

        restLoggingBean.setFilter(new RestLoggingFilter());
        restLoggingBean.setOrder(2);
        restLoggingBean.addUrlPatterns("/*");

        return restLoggingBean;
    }

    @Bean
    public FilterRegistrationBean<AuthFilter> authFilterBean(AuthFilter authFilter){
        FilterRegistrationBean<AuthFilter> authFilterBean =
                new FilterRegistrationBean<>();

        authFilterBean.setFilter(authFilter);
        authFilterBean.setOrder(3);
        authFilterBean.addUrlPatterns("/*");

        return authFilterBean;
    }
}
