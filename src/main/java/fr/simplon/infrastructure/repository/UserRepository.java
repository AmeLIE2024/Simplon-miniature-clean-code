package fr.simplon.infrastructure.repository;

import java.util.ArrayList;
import java.util.List;
import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;
import jakarta.servlet.ServletContext;

public class UserRepository implements UserRepositoryInterface {

    private List<User> userList = new ArrayList<>();
    private ServletContext context;

    public UserRepository(ServletContext context) {
        this.context = context;
    }
    @Override
    public User findByUserName(String username) {
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    @Override
    public User findById(Long userId) {
        for (User user : userList) {
            if (user.getId() == userId) {
                return user;
            }
        }
        return null;
    }

    @Override
    public List<User> findAll(){
        return userList;
        //return (List<User>) context.getContext().getAttribute("users");
    }

    @Override
    public void save(User user) {

        List<User> users = findAll();
        if (users != null && !users.contains(user)) {
            users.add(user);
        }
    }

    @Override
    public void delete (Long userId) {
        List<User> users = findAll();
        users.removeIf(u -> u.getId() == userId);
    }
}
