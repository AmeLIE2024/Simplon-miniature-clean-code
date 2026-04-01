package fr.simplon.presentation.servlets;

import java.io.IOException;

import fr.simplon.application.usecase.LoginUseCase;
import fr.simplon.application.usecase.RegisterUseCase;
import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;
import fr.simplon.domain.services.AuthentificationService;
import fr.simplon.domain.services.SessionService;
import fr.simplon.infrastructure.repository.UserRepository;
import fr.simplon.infrastructure.services.AuthentificationServiceImpl;
import fr.simplon.infrastructure.services.SessionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private SessionService sessionService;
    private AuthentificationService authService;

    @Override
    public void init() throws ServletException {
        UserRepositoryInterface userRepository = new UserRepository();
        LoginUseCase loginUseCase = new LoginUseCase(userRepository);
        RegisterUseCase registerUseCase = new RegisterUseCase(userRepository);

        authService = new AuthentificationServiceImpl(userRepository, loginUseCase, registerUseCase);
        sessionService = new SessionServiceImpl(userRepository);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (sessionService.isUserLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/feeds");
            return;
        }

        req.getRequestDispatcher("/vues/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = authService.handleLogin(username, password);

        if (user != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("username", user.getUsername());
            resp.sendRedirect(req.getContextPath() + "/");
        } else {
            req.setAttribute("error", "Identifiants incorrects.");
            req.getRequestDispatcher("/vues/login.jsp").forward(req, resp);
        }
    }

}
