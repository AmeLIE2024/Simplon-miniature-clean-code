package fr.simplon.infrastructure.repository;

import java.util.List;
import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;

public class UserRepository implements UserRepositoryInterface {

    @Override
    public User findByUserName(String username, List<User> users) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
