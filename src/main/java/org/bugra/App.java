package org.bugra;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main(String[] args) throws LifecycleException {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setHostname("localhost");
        tomcat.getConnector();

        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir.mkdirs();
        }

        tomcat.addWebapp("", webappDir.getAbsolutePath());


        tomcat.start();
        logger.warn("The server is running at: http://localhost:8080/");
        tomcat.getServer().await();
    }
}