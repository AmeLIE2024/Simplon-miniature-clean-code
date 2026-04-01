package fr.simplon.domain.gateway.services;

import java.util.List;

import fr.simplon.domain.models.User;
import jakarta.servlet.http.HttpSession;

public interface SessionService {

    String getLoggedUsername(HttpSession session);

    boolean isUserLoggedIn(HttpSession session);

    User getCurrentUser(HttpSession session, List<User> users);

    void createSession(HttpSession session, String username);

    void invalidateSession(HttpSession session);
}
