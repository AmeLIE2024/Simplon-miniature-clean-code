package fr.simplon.infrastructure.services.tomcat;

import java.io.File;

import fr.simplon.domain.gateway.services.FileStorageService;
import fr.simplon.domain.gateway.services.PostService;
import fr.simplon.domain.gateway.services.SessionService;
import fr.simplon.domain.gateway.services.TomcatService;
import fr.simplon.domain.gateway.strategy.ErrorHandlingStrategy;
import fr.simplon.infrastructure.repository.PostRepository;
import fr.simplon.infrastructure.repository.UserRepository;
import fr.simplon.infrastructure.services.FileStorageServiceImpl;
import fr.simplon.infrastructure.services.SessionServiceImpl;
import fr.simplon.infrastructure.services.post.PostServiceImpl;
import fr.simplon.infrastructure.servlets.PostServlet;
import fr.simplon.infrastructure.strategies.logs.LogStrategyImpl;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class TomcatServiceImpl implements TomcatService {

    private Tomcat tomcat;
    private File publicFolder = new File("src/main/webapp/");
    private Context ctx;
    private ErrorHandlingStrategy errorStrategy;
    private LogStrategyImpl logTomcatStrategy;

    public TomcatServiceImpl(LogStrategyImpl logTomcatStrategy, ErrorHandlingStrategy errorStrategy) {
        this.errorStrategy = errorStrategy;
        this.logTomcatStrategy = logTomcatStrategy;
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
            logTomcatStrategy.logMessage("Public folder already exists!");
        }
    }

    @Override
    public void getContext(Tomcat tomcat) {
        verifyPublicFolderExist(publicFolder);

        this.ctx = tomcat.addWebapp("", publicFolder.getAbsolutePath());
        this.ctx.setReloadable(true);

        PostRepository postRepository = new PostRepository();
        UserRepository userRepository = new UserRepository();
        FileStorageService fileStorageService = new FileStorageServiceImpl(
                publicFolder.getAbsolutePath() + "/uploads",
                "");
        PostService postService = new PostServiceImpl(
                postRepository,
                fileStorageService);
        SessionService sessionService = new SessionServiceImpl(userRepository);

        ctx.getServletContext().setAttribute("postService", postService);
        ctx.getServletContext().setAttribute("sessionService", sessionService);
        ctx.getServletContext().setAttribute("fileStorageService", fileStorageService);

        Tomcat.addServlet(ctx, "postServlet", new PostServlet());
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
