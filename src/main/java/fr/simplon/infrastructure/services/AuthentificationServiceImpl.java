package fr.simplon.infrastructure.services;

import fr.simplon.application.usecase.LoginUseCase;
import fr.simplon.application.usecase.RegisterUseCase;
import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;
import fr.simplon.domain.services.AuthentificationService;

public class AuthentificationServiceImpl implements AuthentificationService {

    private final UserRepositoryInterface userRepository;
    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthentificationServiceImpl(UserRepositoryInterface userRepository, LoginUseCase loginUseCase,
            RegisterUseCase registerUseCase) {
        this.userRepository = userRepository;
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @Override
    public User handleLogin(String username, String password) {
        return loginUseCase.execute(username, password);
    }

    @Override
    public User handleRegister(String username, String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            return null;
        }

        boolean success = registerUseCase.execute(username, password);
        return success ? userRepository.findByUserName(username) : null;
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.findByUserName(username) != null;
    }
}
