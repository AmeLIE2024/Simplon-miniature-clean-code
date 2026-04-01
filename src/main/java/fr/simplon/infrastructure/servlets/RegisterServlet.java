package fr.simplon.infrastructure.servlets;

import java.io.IOException;

import fr.simplon.application.usecase.LoginUseCase;
import fr.simplon.application.usecase.RegisterUseCase;
import fr.simplon.domain.gateway.services.AuthentificationService;
import fr.simplon.domain.models.User;
import fr.simplon.infrastructure.config.AppConfig;
import fr.simplon.infrastructure.repository.UserRepository;
import fr.simplon.infrastructure.services.authentification.AuthentificationServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private RegisterUseCase registerUseCase;
    private AuthentificationService authService;
    private UserRepository userRepository;

    @Override
    public void init() throws ServletException {
        this.userRepository = AppConfig.getUserRepository();
        LoginUseCase loginUseCase = new LoginUseCase(userRepository);
        RegisterUseCase registerUseCase = new RegisterUseCase(userRepository);

        authService = new AuthentificationServiceImpl(userRepository, loginUseCase, registerUseCase);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/vues/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        User user = authService.handleRegister(username, password, confirmPassword);

        if (user != null) {
            resp.sendRedirect(req.getContextPath() + "/login?registered=true");
        } else {
            req.setAttribute("error", "Inscription impossible, vérifiez les champs.");
            req.getRequestDispatcher("/vues/register.jsp").forward(req, resp);
        }
    }
}
