package fr.simplon.infrastructure.services;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import fr.simplon.domain.gateway.TomcatService;

public class TomcatServiceImpl implements TomcatService {

    private Tomcat tomcat;
    private File publicFolder = new File("src/main/webapp/");
    private Context ctx;

    @Override
    public void verifyPublicFolderExist(File publicFolder) {
        if (!publicFolder.exists()) {
            publicFolder.mkdirs();
        }
    }

    @Override
    public void setUpTomcat() {
        this.tomcat = new Tomcat();
        this.tomcat.setBaseDir(".");
        this.tomcat.setPort(8080);

        readClasses();

        this.tomcat.getConnector();

        try {
            this.tomcat.start();
            this.tomcat.getServer().await();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getContext(Tomcat tomcat) {
        verifyPublicFolderExist(publicFolder);

        this.ctx = tomcat.addWebapp("", publicFolder.getAbsolutePath());
        this.ctx.setReloadable(true);
    }

    @Override
    public void readClasses() {
        getContext(tomcat);

        File classFolder = new File("build/classes/java/main");
        WebResourceRoot resourceRoot = new StandardRoot(ctx);
        resourceRoot.addPreResources(
                new DirResourceSet(resourceRoot, "/WEB-INF/classes", classFolder.getAbsolutePath(), "/vues/"));
    }
}
