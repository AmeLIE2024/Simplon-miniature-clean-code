package fr.simplon.domain.repository;

import java.util.List;

import fr.simplon.domain.models.User;

public interface UserRepositoryInterface {
    User findByUserName(String username);
    User findById(Long id);
    List<User> findAll();
    void save(User user);
    void delete (Long userId);
}
