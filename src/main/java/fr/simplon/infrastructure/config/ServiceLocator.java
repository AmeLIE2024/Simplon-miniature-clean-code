package fr.simplon.infrastructure.config;

import fr.simplon.application.usecase.LoginUseCase;
import fr.simplon.application.usecase.RegisterUseCase;
import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;
import fr.simplon.infrastructure.repository.UserRepository;

public class ServiceLocator {
    private static ServiceLocator instance;

    private final UserRepositoryInterface userRepository;
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    private ServiceLocator() {
        this.userRepository = new UserRepository();

        this.userRepository.save(new User("admin", "admin"));
        this.userRepository.save(new User("user", "password"));

        this.loginUseCase = new LoginUseCase(userRepository);
        this.registerUseCase = new RegisterUseCase(userRepository);
    }

    public static synchronized ServiceLocator getInstance() {
        if (instance == null) {
            instance = new ServiceLocator();
        }

        return instance;
    }

    public LoginUseCase getLoginUseCase() {
        return loginUseCase;
    }

    public RegisterUseCase gRegisterUseCase() {
        return registerUseCase;
    }

}
