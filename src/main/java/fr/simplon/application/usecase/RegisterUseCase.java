package fr.simplon.application.usecase;

import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;

public class RegisterUseCase {

    private final UserRepositoryInterface userRepository;

    public RegisterUseCase(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    public boolean execute(String username, String password) {
        if (userRepository.findByUserName(username) != null) {
            return false;
        }

        userRepository.save(new User(username, password));
        return true;
    }

}
