package fr.simplon.infrastructure.servlets;

import java.io.IOException;

import fr.simplon.domain.gateway.ErrorHandlingStrategy;
import fr.simplon.domain.gateway.FileStorageService;
import fr.simplon.domain.gateway.PostService;
import fr.simplon.domain.repository.UserRepositoryInterface;
import fr.simplon.infrastructure.repository.UserRepository;
import fr.simplon.infrastructure.services.FileStorageServiceImpl;
import fr.simplon.infrastructure.services.post.PostServiceImpl;
import fr.simplon.infrastructure.strategies.errors.ErrorHandlingStrategyImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/post/*")
public class PostServlet extends HttpServlet {
    private PostService postService;
    private UserRepositoryInterface userRepository;
    private ErrorHandlingStrategy errorHandlingStrategy;
    private FileStorageService fileStorageService;

    public PostServlet(PostService postService) {
        this.postService = new PostServiceImpl();
        this.userRepository = new UserRepository();
        this.errorHandlingStrategy = new ErrorHandlingStrategyImpl();
        this.fileStorageService = new FileStorageServiceImpl();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getPathInfo();

        switch (action) {
            case "/like" -> handleLike(req, resp);
            case "/comment" -> handleNewComment(req, resp);
            case "/follow" -> handleFollowRequest(req, resp);
            case "/new" -> handleNewPost(req, resp);
            default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }








}
