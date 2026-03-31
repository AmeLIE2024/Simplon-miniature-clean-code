package fr.simplon.domain.gateway;

import java.io.File;

import org.apache.catalina.startup.Tomcat;

public interface TomcatService {

    public void setUpTomcat();

    public void getContext(Tomcat tomcat);

    public void readClasses();
}
