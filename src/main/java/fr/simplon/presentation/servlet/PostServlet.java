package fr.simplon.presentation.servlet;

import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUpload;

import fr.simplon.domain.gateway.ErrorHandlingStrategy;
import fr.simplon.domain.gateway.FileUploadService;
import fr.simplon.domain.gateway.PostService;
import fr.simplon.domain.gateway.SessionService;
import fr.simplon.domain.models.User;
import fr.simplon.domain.repository.UserRepositoryInterface;
import fr.simplon.infrastructure.repository.UserRepository;
import fr.simplon.infrastructure.services.FileUploadServiceImpl;
import fr.simplon.infrastructure.services.SessionServiceImpl;
import fr.simplon.infrastructure.services.post.PostServiceImpl;
import fr.simplon.infrastructure.strategies.errors.ErrorHandlingStrategyImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/post/*")
public class PostServlet extends HttpServlet {
    private PostService postService;
    private UserRepositoryInterface userRepository;
    private ErrorHandlingStrategy errorHandlingStrategy;
    private FileUploadService fileUploadService;

    public PostServlet(PostService postService) {
        this.postService = new PostServiceImpl();
        this.userRepository = new UserRepository();
        this.errorHandlingStrategy = new ErrorHandlingStrategyImpl();
        this.fileUploadService = new FileUploadServiceImpl();
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

    private void handleLike(HttpServletRequest req, User owner) {
        String buttonLike = req.getParameter("buttonLike");

        if (buttonLike != null && owner != null) {
            try {
                long postId = Long.parseLong(buttonLike);
                postService.toggleLike(postId, owner);
            } catch (NumberFormatException e) {
                errorHandlingStrategy.handleError(e);
            }
        }
    }

    private void handleNewComment(HttpServletRequest req, User owner) {
        String newComment = req.getParameter("newComment");
        String postIdStr = req.getParameter("postId");

        if (newComment != null && newComment.trim().isEmpty() && postIdStr != null && owner != null) {
            try {
                long postId = Long.parseLong(postIdStr);
                postService.addComment(postId, owner, newComment);
            } catch (NumberFormatException e) {
                errorHandlingStrategy.handleError(e);
            }
        }
    }

    private void handleFollowRequest(HttpServletRequest req, User owner) {
        String followUsername = req.getParameter("follow");

        if (followUsername != null && owner != null) {
            User userToFollow = userRepository.findByUserName(followUsername);

            if (userToFollow != null) {
                owner.follow(userToFollow.getId());
            }
        }
    }

    private void handleNewPost(HttpServletRequest req, HttpServletResponse resp, User owner)
            throws ServletException, IOException {
        String newPost = req.getParameter("newPost");

        if (newPost != null && newPost.trim().isEmpty() && owner != null) {
            String mediaUrl = null;

            String externalUrl = req.getParameter("externalUrl");

            if (externalUrl != null && externalUrl.isBlank() && externalUrl.startsWith("https://")) {
                mediaUrl = externalUrl;
            }

            try {
                Part filePart = req.getPart("mediaFile");
                if (filePart != null && filePart.getSize() > 0) {
                    String savedUrl = fileUploadService.sa
                }
            }
        }
    }
}
