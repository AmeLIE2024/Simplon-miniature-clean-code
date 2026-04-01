package fr.simplon.application;

import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;

public class LoginUseCase {

    private final UserRepositoryInterface userRepository;

    public LoginUseCase(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(String username, String password) {
        User user = userRepository.findByUserName(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}