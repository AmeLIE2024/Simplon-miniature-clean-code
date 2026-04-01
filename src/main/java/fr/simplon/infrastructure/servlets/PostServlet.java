package fr.simplon.infrastructure.servlets;

import fr.simplon.domain.gateway.services.FileStorageService;
import fr.simplon.domain.gateway.services.PostService;
import fr.simplon.domain.gateway.services.SessionService;
import fr.simplon.domain.models.AttachmentType;
import fr.simplon.domain.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/feeds")
public class PostServlet extends HttpServlet {

    private SessionService sessionService;
    private FileStorageService fileStorageService;
    private PostService postService;

    @Override
    public void init() {

        var context = getServletContext();

        this.sessionService = (SessionService) context.getAttribute("sessionService");
        this.fileStorageService = (FileStorageService) context.getAttribute("fileStorageService");
        this.postService = (PostService) context.getAttribute("postService");

        if (sessionService == null || fileStorageService == null || postService == null) {
            throw new IllegalStateException("❌ Dépendances non initialisées dans le context");
        }

        System.out.println("[PostServlet] init OK");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!sessionService.isUserLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User currentUser = sessionService.getCurrentUser(req.getSession(false), getUsers());

        if (currentUser != null) {
            req.setAttribute("currentUserId", currentUser.getId());
        }

        String feedType = req.getParameter("type") != null
                ? req.getParameter("type")
                : "recommandations";

        req.setAttribute("feedType", feedType);
        req.getRequestDispatcher("/vues/feeds.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (!sessionService.isUserLoggedIn(req.getSession(false))) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User owner = sessionService.getCurrentUser(req.getSession(false), getUsers());
        String action = req.getParameter("action");

        if (action == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {

            case "like" -> postService.toggleLike(
                    Long.parseLong(req.getParameter("postId")),
                    Long.parseLong(req.getParameter("userId")));

            case "comment" -> postService.addComment(
                    Long.parseLong(req.getParameter("postId")),
                    owner,
                    req.getParameter("comment"));

            case "follow" -> postService.followUser(
                    Long.parseLong(req.getParameter("targetUserId")),
                    owner);

            case "new" -> {
                String content = req.getParameter("content");
                String externalUrl = req.getParameter("externalUrl");

                String mediaUrl = null;
                AttachmentType attachmentType = AttachmentType.NONE;

                if (externalUrl != null && !externalUrl.isBlank() && externalUrl.startsWith("https://")) {
                    mediaUrl = externalUrl;
                    attachmentType = AttachmentType.EXTERNAL;
                }

                Part filePart = req.getPart("mediaFile");

                if (filePart != null && filePart.getSize() > 0) {

                    String fileName = filePart.getSubmittedFileName();
                    String extension = getExtension(fileName);

                    // ✅ validation ici (responsabilité métier)
                    if (!postService.checkExtension(extension)) {
                        resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                        return;
                    }

                    mediaUrl = fileStorageService.saveFile(
                            fileName,
                            filePart.getInputStream());

                    attachmentType = AttachmentType.fromExtension(fileName);
                }

                postService.createPost(content, mediaUrl, owner, attachmentType);
            }

            default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains("."))
            return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private List<User> getUsers() {
        return (List<User>) getServletContext().getAttribute("users");
    }
}