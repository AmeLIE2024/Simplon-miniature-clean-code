package fr.simplon.presentation.listeners;

import fr.simplon.application.usecase.LoginUseCase;
import fr.simplon.application.usecase.RegisterUseCase;
import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;
import fr.simplon.domain.services.AuthentificationService;
import fr.simplon.domain.services.FileStorageService;
import fr.simplon.domain.services.PostService;
import fr.simplon.domain.services.SessionService;
import fr.simplon.infrastructure.repository.PostRepository;
import fr.simplon.infrastructure.repository.UserRepository;
import fr.simplon.infrastructure.services.AuthentificationServiceImpl;
import fr.simplon.infrastructure.services.FileStorageServiceImpl;
import fr.simplon.infrastructure.services.PostServiceImpl;
import fr.simplon.infrastructure.services.SessionServiceImpl;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        String uploadDir = ctx.getRealPath("/uploads");
        String contextPath = ctx.getContextPath();

        UserRepositoryInterface userRepository = new UserRepository();
        userRepository.save(new User("admin", "admin"));
        userRepository.save(new User("user", "password"));

        PostRepository postRepository = new PostRepository();

        LoginUseCase loginUseCase = new LoginUseCase(userRepository);
        RegisterUseCase registerUseCase = new RegisterUseCase(userRepository);

        FileStorageService fileStorageService = new FileStorageServiceImpl(uploadDir, contextPath);
        PostService postService = new PostServiceImpl(postRepository, fileStorageService);
        SessionService sessionService = new SessionServiceImpl(userRepository);
        AuthentificationService authService = new AuthentificationServiceImpl(userRepository, loginUseCase,
                registerUseCase);

        ctx.setAttribute("authService", authService);
        ctx.setAttribute("sessionService", sessionService);
        ctx.setAttribute("postService", postService);
        ctx.setAttribute("fileStorageService", fileStorageService);

        System.out.println("[AppContextListener] Services initialisés.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("[AppContextListener] Application arrêtée.");
    }
}