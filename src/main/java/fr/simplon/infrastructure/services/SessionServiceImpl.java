package fr.simplon.infrastructure.services;

import java.util.List;

import fr.simplon.domain.gateway.services.SessionService;
import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;
import jakarta.servlet.http.HttpSession;

public class SessionServiceImpl implements SessionService {

    private UserRepositoryInterface userRepository;

    public SessionServiceImpl() {
    }

    public SessionServiceImpl(UserRepositoryInterface userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String getLoggedUsername(HttpSession session) {

        if (session != null) {
            return (String) session.getAttribute("loggedUser");
        } else {
            System.out.println("Session is null"); // TODO : Faire un log pour la session
            return null;
        }
    }

    @Override
    public boolean isUserLoggedIn(HttpSession session) {
        return session != null && getLoggedUsername(session) != null;
    }

    @Override
    public User getCurrentUser(HttpSession session, List<User> users) {

        String username = getLoggedUsername(session);
        User currentUser = userRepository.findByUserName(username);

        if (currentUser != null) {
            return currentUser;
        }

        return null;
    }

    @Override
    public void createSession(HttpSession session, String username) {
        session.setAttribute("loggedUser", username);
        session.setMaxInactiveInterval(60 * 60);
    }

    @Override
    public void invalidateSession(HttpSession session) {
        if (session != null) {
            session.invalidate();
        }
    }

}
