package org.miniature;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class App {

    public static void main(String[] args) {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        File publicFolder = new File("src/main/webapp/");
        if (!publicFolder.exists()) {
            publicFolder.mkdirs();
        }

        // Lire le dossier webapp/ pour les fichiers statiques et les JSP
        Context ctx = tomcat.addWebapp("", publicFolder.getAbsolutePath());
        ctx.setReloadable(true);

        // Lire les classes Java avec l'annotation @WebServlet automatiquement
        File classFolder = new File("build/classes/java/main");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(
                new DirResourceSet(resources, "/WEB-INF/classes", classFolder.getAbsolutePath(), "/"));
        ctx.setResources(resources);

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }

        tomcat.getServer().await();
    }
}