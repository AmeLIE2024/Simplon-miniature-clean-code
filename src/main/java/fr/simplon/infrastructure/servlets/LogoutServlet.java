package fr.simplon.infrastructure.servlets;

import fr.simplon.domain.gateway.services.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet({"/logout"})
public class LogoutServlet extends HttpServlet {

    private SessionService sessionService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       if(sessionService != null) {
           sessionService.invalidateSession(req.getSession(false));
       } else {
           resp.sendRedirect(req.getContextPath() + "/login");
       }

    }
}
