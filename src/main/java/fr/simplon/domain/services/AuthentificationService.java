package fr.simplon.domain.services;

import fr.simplon.domain.models.User;

public interface AuthentificationService {
    User handleLogin(String username, String password);

    User handleRegister(String username, String password, String confirmPassword);

    boolean usernameExists(String username);
}
