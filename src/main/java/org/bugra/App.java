package org.bugra;

import org.bugra.config.StorageConfig;
import org.bugra.ui.AppUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App{
    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {

        logger.info("Program is about to start...");
        ApplicationContext context = new AnnotationConfigApplicationContext(StorageConfig.class);

        AppUI ui = context.getBean(AppUI.class);

        ui.run();

    }
}
