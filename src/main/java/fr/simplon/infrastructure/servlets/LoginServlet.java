package fr.simplon.infrastructure.servlets;

import java.util.ArrayList;
import java.util.List;

import fr.simplon.domain.models.User;
import jakarta.servlet.http.HttpServlet;

public class LoginServlet extends HttpServlet {

    private List<User> userList = new ArrayList<>();

    @Override
    public void init() {

    }

}
