package fr.simplon.domain.gateway;

import java.util.List;

import fr.simplon.domain.models.User;
import jakarta.servlet.http.HttpSession;

public interface SessionService {

    void checkExtensions();

    String getLoggedUsername(HttpSession session);

    boolean isUserLoggedIn(HttpSession session);

    User getCurrentUser(HttpSession session, List<User> users);
}
