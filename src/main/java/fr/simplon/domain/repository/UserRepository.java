package fr.simplon.domain.repository;

import java.util.List;

import fr.simplon.domain.models.User;

public interface UserRepository {
    public User findByUserName(String username, List<User> users);
}
