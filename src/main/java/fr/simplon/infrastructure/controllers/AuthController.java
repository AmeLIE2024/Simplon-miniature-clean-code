package fr.simplon.infrastructure.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.simplon.domain.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(urlPatterns = { "/login", "/register" })
public class AuthController extends HttpServlet {

    private List<User> users = new ArrayList<>();

    @Override
    public void init() {
        if (users.isEmpty()) {
            users.add(new User(1L, "admin", "admin123"));
            users.add(new User(2L, "orane", "1234"));
            getServletContext().setAttribute("users", users);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/register".equals(path)) {
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/feeds");
            return;
        }

        req.getRequestDispatcher("/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(req, resp);
        } else if ("/register".equals(path)) {
            handleRegister(req, resp);
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (!isValidInput(username, password)) {
            req.setAttribute("error", "Tous les champs sont obligatoires.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
            return;
        }

        User userFound = findUser(username.trim(), password.trim());

        if (userFound != null) {
            createSession(req, userFound.getUsername());
            resp.sendRedirect(req.getContextPath() + "/feeds");
        } else {
            req.setAttribute("error", "Identifiants incorrects.");
            req.getRequestDispatcher("/login.jsp").forward(req, resp);
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String confirmPassword = req.getParameter("confirmPassword");

        if (!isValidInput(username, password, confirmPassword)) {
            req.setAttribute("error", "Tous les champs sont obligatoires.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        for (User user : users) {
            if (user.getUsername().equals(username.trim())) {
                req.setAttribute("error", "Ce nom d'utilisateur est déjà pris.");
                req.getRequestDispatcher("/register.jsp").forward(req, resp);
                return;
            }
        }

        if (!password.equals(confirmPassword)) {
            req.setAttribute("error", "Les mots de passe ne correspondent pas.");
            req.getRequestDispatcher("/register.jsp").forward(req, resp);
            return;
        }

        users.add(new User(username.trim(), password));
        resp.sendRedirect(req.getContextPath() + "/login?registered=true");
    }

    /*
     * varargs (String... fields) permet de passer autant de String sans définir le
     * nombre
     * à l'avance
     */
    private boolean isValidInput(String... fields) {
        for (String field : fields) {
            if (field == null || field.trim().isEmpty())
                return false;
        }
        return true;
    }

    private User findUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username)
                    && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    private void createSession(HttpServletRequest req, String username) {
        HttpSession old = req.getSession(false);
        if (old != null) {
            old.invalidate();
        }

        HttpSession session = req.getSession(true);
        session.setAttribute("loggedUser", username);
        session.setMaxInactiveInterval(60 * 60);

        System.out.println("=== SESSION DEBUG ===");
        System.out.println("ID : " + session.getId());
        System.out.println("loggedUser : " + session.getAttribute("loggedUser"));
    }
}
