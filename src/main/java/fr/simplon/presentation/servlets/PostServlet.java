package fr.simplon.presentation.servlets;

import fr.simplon.domain.models.AttachmentType;
import fr.simplon.domain.models.User;
import fr.simplon.domain.services.FileStorageService;
import fr.simplon.domain.services.PostService;
import fr.simplon.domain.services.SessionService;
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
    public void init() throws ServletException {

        var context = getServletContext();

        try {
            Object ss = context.getAttribute("sessionService");
            Object fs = context.getAttribute("fileStorageService");
            Object ps = context.getAttribute("postService");

            if (!(ss instanceof SessionService)) {
                throw new IllegalStateException("sessionService invalide ou non initialisé");
            }
            if (!(fs instanceof FileStorageService)) {
                throw new IllegalStateException("fileStorageService invalide ou non initialisé");
            }
            if (!(ps instanceof PostService)) {
                throw new IllegalStateException("postService invalide ou non initialisé");
            }

            this.sessionService = (SessionService) ss;
            this.fileStorageService = (FileStorageService) fs;
            this.postService = (PostService) ps;

            System.out.println("[PostServlet] init OK");

        } catch (ClassCastException e) {
            throw new ServletException(
                    "Problème de ClassLoader (doublon d'interface ou dépendance)", e);
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
        if (feedType == null)
            feedType = "recommandations";

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
                AttachmentType type = AttachmentType.NONE;

                if (externalUrl != null && externalUrl.startsWith("https://")) {
                    mediaUrl = externalUrl;
                    type = AttachmentType.EXTERNAL;
                }

                Part filePart = req.getPart("mediaFile");

                if (filePart != null && filePart.getSize() > 0) {

                    String fileName = filePart.getSubmittedFileName();
                    String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();

                    if (!postService.checkExtension(extension)) {
                        resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                        return;
                    }

                    mediaUrl = fileStorageService.saveFile(
                            fileName,
                            filePart.getInputStream());

                    type = AttachmentType.fromExtension(fileName);
                }

                postService.createPost(content, mediaUrl, owner, type);
            }

            default -> resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}