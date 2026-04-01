package fr.simplon.infrastructure.config;

import fr.simplon.domain.gateway.services.*;
import fr.simplon.domain.repository.UserRepositoryInterface;
import fr.simplon.infrastructure.repository.PostRepository;
import fr.simplon.infrastructure.repository.UserRepository;
import fr.simplon.infrastructure.services.*;
import fr.simplon.infrastructure.services.authentification.AuthentificationServiceImpl;
import fr.simplon.infrastructure.services.post.PostServiceImpl;
import fr.simplon.application.usecase.*;

public class AppConfig {
    private static final UserRepository userRepository = new UserRepository();
    private static final PostRepository postRepository = new PostRepository();
    private static final LoginUseCase loginUseCase = new LoginUseCase(userRepository);
    private static final RegisterUseCase registerUseCase = new RegisterUseCase(userRepository);
    private static final SessionService sessionService = new SessionServiceImpl(userRepository);
    private static final AuthentificationService authenticationService = new AuthentificationServiceImpl(userRepository,
            loginUseCase, registerUseCase);
    private static final PostService postService = new PostServiceImpl(postRepository);

    public static UserRepository getUserRepository() {
        return userRepository;
    }

    public static PostRepository getPostRepository() {
        return postRepository;
    }

    public static SessionService getSessionService() {
        return sessionService;
    }

    public static AuthentificationService getAuthenticationService() {
        return authenticationService;
    }

    public static PostService getPostService() {
        return postService;
    }
}