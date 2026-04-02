package fr.simplon.presentation.servlets;

import fr.simplon.domain.models.AttachmentType;
import fr.simplon.domain.models.User;
import fr.simplon.domain.services.FileStorageService;
import fr.simplon.domain.services.PostService;
import fr.simplon.domain.services.SessionService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/feeds")
@MultipartConfig
public class PostServlet extends HttpServlet {

    private SessionService sessionService;
    private FileStorageService fileStorageService;
    private PostService postService;

    @Override
    public void init() throws ServletException {
        var context = getServletContext();

        // on injecte les services par notre AppContexListener au démarrage
        this.sessionService = (SessionService) context.getAttribute("sessionService");
        this.fileStorageService = (FileStorageService) context.getAttribute("fileStorageService");
        this.postService = (PostService) context.getAttribute("postService");

        if (this.sessionService == null || this.fileStorageService == null || this.postService == null) {
            throw new ServletException(
                    "Services non initialisés.");
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

        List<User> users = (List<User>) getServletContext().getAttribute("users");
        User currentUser = sessionService.getCurrentUser(session, users);

        if (currentUser != null) {
            req.setAttribute("currentUserId", currentUser.getId());
        }

        String feedType = req.getParameter("type");
        if (feedType == null) {
            feedType = "recommandations";
        }

        req.setAttribute("feedType", feedType);
        req.getRequestDispatcher("/vues/feeds.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);

        if (!sessionService.isUserLoggedIn(session)) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<User> users = (List<User>) getServletContext().getAttribute("users");
        User owner = sessionService.getCurrentUser(session, users);

        String action = req.getParameter("action");

        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètre 'action' manquant");
            return;
        }

        switch (action) {

            case "like" -> {
                Long postId = parseLongParam(req.getParameter("postId"));
                Long userId = parseLongParam(req.getParameter("userId"));
                if (postId == null || userId == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
                    return;
                }
                postService.toggleLike(postId, userId);
            }

            case "comment" -> {
                Long postId = parseLongParam(req.getParameter("postId"));
                String comment = req.getParameter("comment");
                if (postId == null || comment == null || comment.isBlank()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
                    return;
                }
                postService.addComment(postId, owner, comment);
            }

            case "follow" -> {
                Long targetUserId = parseLongParam(req.getParameter("targetUserId"));
                if (targetUserId == null) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres invalides");
                    return;
                }
                postService.followUser(targetUserId, owner);
            }

            case "new" -> handleNewPost(req, resp, owner);

            default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action inconnue");
        }
    }

    private void handleNewPost(HttpServletRequest req, HttpServletResponse resp, User owner)
            throws ServletException, IOException {

        String content = req.getParameter("content");
        if (content == null || content.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Contenu manquant");
            return;
        }

        String mediaUrl = null;
        AttachmentType type = AttachmentType.NONE;

        String externalUrl = req.getParameter("externalUrl");
        if (externalUrl != null && !externalUrl.isBlank()) {
            if (!externalUrl.startsWith("https://")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL externe invalide (HTTPS requis)");
                return;
            }
            mediaUrl = externalUrl;
            type = AttachmentType.EXTERNAL;
        }

        Part filePart = req.getPart("mediaFile");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = filePart.getSubmittedFileName();
            if (fileName == null || !fileName.contains(".")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nom de fichier invalide");
                return;
            }

            String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
            if (!postService.checkExtension(extension)) {
                resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Extension non autorisée");
                return;
            }

            mediaUrl = fileStorageService.saveFile(fileName, filePart.getInputStream());
            type = AttachmentType.fromExtension(fileName);
        }

        postService.createPost(content, mediaUrl, owner, type);
    }

    private Long parseLongParam(String value) {
        if (value == null || value.isBlank())
            return null;
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}