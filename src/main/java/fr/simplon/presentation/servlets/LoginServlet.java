package fr.simplon.presentation.servlets;

import java.io.IOException;

import fr.simplon.application.usecase.LoginUseCase;
import fr.simplon.domain.models.User;
import fr.simplon.domain.services.AuthentificationService;
import fr.simplon.domain.services.SessionService;
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
        var ctx = getServletContext();
        this.sessionService = (SessionService) ctx.getAttribute("sessionService");
        this.authService = (AuthentificationService) ctx.getAttribute("authService");

        if (sessionService == null || authService == null) {
            throw new ServletException("Services manquants");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (sessionService.isUserLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/feeds");
            return;
        }
        req.getRequestDispatcher("/vues/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        User user = authService.handleLogin(username, password);

        if (user != null) {
            HttpSession session = req.getSession(true);
            sessionService.createSession(session, user.getUsername());
            resp.sendRedirect(req.getContextPath() + "/feeds");
        } else {
            req.setAttribute("error", "Identifiants incorrects.");
            req.getRequestDispatcher("/vues/login.jsp").forward(req, resp);
        }
    }
}
