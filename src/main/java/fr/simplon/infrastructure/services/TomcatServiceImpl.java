package fr.simplon.infrastructure.services;

import java.io.File;

import fr.simplon.domain.gateway.ErrorHandlingStrategy;
import fr.simplon.domain.gateway.FileStorageService;
import fr.simplon.domain.gateway.PostService;
import fr.simplon.domain.gateway.SessionService;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import fr.simplon.domain.gateway.TomcatService;
import fr.simplon.infrastructure.controllers.PostController;
import fr.simplon.infrastructure.repository.PostRepository;
import fr.simplon.infrastructure.services.post.PostServiceImpl;
import fr.simplon.infrastructure.servlets.PostServlet;
import fr.simplon.infrastructure.strategies.logs.LogStrategyImpl;

public class TomcatServiceImpl implements TomcatService {

    private Tomcat tomcat;
    private File publicFolder = new File("src/main/webapp/");
    private Context ctx;
    private ErrorHandlingStrategy errorStrategy;
    private LogStrategyImpl logStrategy;

    public TomcatServiceImpl(LogStrategyImpl logStrategy, ErrorHandlingStrategy errorStrategy) {
        this.logStrategy = logStrategy;
        this.errorStrategy = errorStrategy;
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
            errorStrategy.handleError(e);
        }
    }

    public void verifyPublicFolderExist(File publicFolder) {
        if (!publicFolder.exists()) {
            publicFolder.mkdirs();
        }
    }

    @Override
    public void getContext(Tomcat tomcat) {
        verifyPublicFolderExist(publicFolder);

        this.ctx = tomcat.addWebapp("", publicFolder.getAbsolutePath());
        this.ctx.setReloadable(true);

        PostService postService = new PostServiceImpl(new PostRepository());
        SessionService sessionService = new SessionServiceImpl();
        FileStorageService fileStorageService = new FileStorageServiceImpl(
                publicFolder.getAbsolutePath() + "/uploads",
                "", postService);
        PostController postController = new PostController(postService);

        PostServlet postServlet = new PostServlet(postController, sessionService, fileStorageService);

        Tomcat.addServlet(ctx, "postServlet", postServlet);
        ctx.addServletMappingDecoded("/feeds", "postServlet");
    }

    @Override
    public void readClasses() {
        getContext(tomcat);

        File classFolder = new File("build/classes/java/main");
        verifyPublicFolderExist(classFolder);

        WebResourceRoot resourceRoot = new StandardRoot(ctx);
        resourceRoot.addPreResources(
                new DirResourceSet(resourceRoot, "/WEB-INF/classes", classFolder.getAbsolutePath(), "/"));
        ctx.setResources(resourceRoot);
    }
}
