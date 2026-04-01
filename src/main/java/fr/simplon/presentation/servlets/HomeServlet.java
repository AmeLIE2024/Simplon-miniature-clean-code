package fr.simplon.presentation.servlets;

import java.io.IOException;

import fr.simplon.infrastructure.services.SessionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private final SessionServiceImpl sessionService = new SessionServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        boolean loggedUser = sessionService.isUserLoggedIn(session);

        if (!loggedUser) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        req.setAttribute("loggedUser", session.getAttribute("loggedUser"));
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}