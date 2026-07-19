package org.bugra.config;

import jakarta.servlet.*;
import org.bugra.filter.RestLoggingFilter;
import org.bugra.filter.TransactionFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.EnumSet;

public class AppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(final ServletContext servletContext) {

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        context.setServletContext(servletContext);
        context.refresh();

        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        // add transaction id filter to beginning
        FilterRegistration.Dynamic transactionIdFilter =
                servletContext.addFilter("transactionIdFilter", new TransactionFilter());

        transactionIdFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), false, "/*");

        // filter for logging for incoming request
        FilterRegistration.Dynamic restLoggingFilter =
                servletContext.addFilter("restLoggingFilter", new RestLoggingFilter());

        restLoggingFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST),true, "/*");

        // filter for auth validation
        FilterRegistration.Dynamic authFilter =
                servletContext.addFilter("authFilter", new DelegatingFilterProxy("authFilter"));

        authFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST),true, "/*");
    }
}