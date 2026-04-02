package fr.simplon.presentation.servlets;

import java.io.IOException;

import fr.simplon.domain.services.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

    private SessionService sessionService;

    @Override
    public void init() throws ServletException {
        this.sessionService = (SessionService) getServletContext().getAttribute("sessionService");

        if (sessionService == null) {
            throw new ServletException("sessionService manquant — AppContextListener enregistré ?");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (!sessionService.isUserLoggedIn(session)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        req.setAttribute("loggedUser", session.getAttribute("username"));
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}