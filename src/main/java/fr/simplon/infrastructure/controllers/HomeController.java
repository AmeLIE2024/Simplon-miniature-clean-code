package fr.simplon.infrastructure.controllers;

import java.io.IOException;

import fr.simplon.infrastructure.services.SessionServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet({ "/home" })
public class HomeController extends HttpServlet {

    private SessionServiceImpl sessionService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

       boolean loggedUser = sessionService.isUserLoggedIn(session);

       if (loggedUser) {
           resp.sendRedirect(req.getContextPath() + "/login");
       } else {
           req.getRequestDispatcher("/index.jsp").forward(req, resp);
       }

//        if (session == null || session.getAttribute("loggedUser") == null) {
//            resp.sendRedirect(req.getContextPath() + "/login");
//            return;
//        }

//        String loggedUser = (String) session.getAttribute("loggedUser");
//        req.setAttribute("loggedUser", loggedUser);
//        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}
