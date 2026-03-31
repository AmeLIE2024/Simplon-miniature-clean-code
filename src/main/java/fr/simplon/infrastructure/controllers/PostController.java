package fr.simplon.infrastructure.controllers;

import fr.simplon.domain.gateway.PostService;
import fr.simplon.domain.repository.UserRepositoryInterface;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

import fr.simplon.domain.gateway.SessionService;
import fr.simplon.domain.models.ImageExtension;
import fr.simplon.domain.models.Post;
import fr.simplon.domain.models.User;
import fr.simplon.domain.models.VideoExtension;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
@WebServlet("/feeds")
public class PostController extends HttpServlet {

    private List<Post> postList = new ArrayList<>();
    private ImageExtension imageExtension;
    private VideoExtension videoExtension;

    private SessionService sessionService;
    private PostService postService;
    private UserRepositoryInterface userRepository;

    public PostController(SessionService sessionService, PostService postService,
            UserRepositoryInterface userRepository) {
        this.sessionService = sessionService;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        User currentUser = sessionService.getCurrentUser(session, getUsers());

        if (currentUser != null) {
            req.setAttribute("currentUserId", currentUser.getId());
        }

        String feedType = req.getParameter("type") != null ? req.getParameter("type") : "recommandations";
        List<Post> postToShow = postService.getPostsByFeedType(feedType, currentUser, postList);

        req.setAttribute("feedType", feedType);
        req.setAttribute("postList", postToShow);
        req.getRequestDispatcher("/vues/feeds.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        if (!sessionService.isUserLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User owner = sessionService.getCurrentUser(req.getSession(false), getUsers());

    }

    private List<User> getUsers() {
        return (List<User>) getServletContext().getAttribute("users");
    }

}
