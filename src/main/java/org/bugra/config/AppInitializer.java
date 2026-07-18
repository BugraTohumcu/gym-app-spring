package org.bugra.config;

import jakarta.servlet.*;
import org.bugra.filter.TransactionFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.EnumSet;

public class AppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(final ServletContext servletContext) {

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);

        ServletRegistration.Dynamic dispatcher =
                servletContext.addServlet("dispatcher", new DispatcherServlet(context));
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

        // add transaction id filter
        FilterRegistration.Dynamic transactionIdFilter =
                servletContext.addFilter("transactionIdFilter", new TransactionFilter());

        transactionIdFilter.addMappingForUrlPatterns(
                EnumSet.of(DispatcherType.REQUEST), false, "/*");
    }
}