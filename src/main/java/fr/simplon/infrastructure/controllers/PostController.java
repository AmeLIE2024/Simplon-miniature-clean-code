package fr.simplon.infrastructure.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.*;

import fr.simplon.domain.models.AttachmentType;
import fr.simplon.domain.models.Post;
import fr.simplon.domain.models.User;

@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 5 * 1024 * 1024, maxRequestSize = 10 * 1024 * 1024)
@WebServlet("/feeds")
public class PostController extends HttpServlet {

    private List<Post> postList = new ArrayList<>();
    private static final Set<String> AUTHORIZED_IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> AUTHORIZED_VIDEO_EXTENSIONS = Set.of("mp4", "webm", "wav");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            String username = (String) session.getAttribute("loggedUser");
            List<User> users = (List<User>) getServletContext().getAttribute("users");
            User currentUser = findByUserName(username, users);
            if (currentUser != null) {
                req.setAttribute("currentUserId", currentUser.getId());
            }
        }

        String feedType = req.getParameter("type");
        if (feedType == null)
            feedType = "recommendations";

        List<Post> postsToShow = postList;

        if (session != null && session.getAttribute("loggedUser") != null) {
            String username = (String) session.getAttribute("loggedUser");
            List<User> users = (List<User>) getServletContext().getAttribute("users");
            User currentUser = findByUserName(username, users);

            if ("subscriptions".equals(feedType) && currentUser != null) {
                postsToShow = new ArrayList<>();
                for (Post post : postList) {
                    if (currentUser.isFollowing(post.getOwner())) {
                        postsToShow.add(post);
                    }
                }
            }
        }

        req.setAttribute("feedType", feedType);
        req.setAttribute("postList", postsToShow);
        req.getRequestDispatcher("/feeds.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String username = (String) session.getAttribute("loggedUser");
        List<User> users = (List<User>) getServletContext().getAttribute("users");
        User owner = findByUserName(username, users);

        String newPost = req.getParameter("newPost");
        String newComment = req.getParameter("newComment");
        String buttonLike = req.getParameter("buttonLike");
        String postIdStr = req.getParameter("postId");
        String followUsername = req.getParameter("follow");

        if (followUsername != null && owner != null) {
            User userToFollow = findByUserName(followUsername, users);
            if (userToFollow != null) {
                owner.follow(userToFollow.getId());
            }
        } else if (newPost != null && !newPost.trim().isEmpty()) {
            if (owner != null) {
                String mediaUrl = null;
                AttachmentType attachmentType = AttachmentType.NONE;

                String externalUrl = req.getParameter("externalUrl");
                if (externalUrl != null && !externalUrl.isBlank() && externalUrl.startsWith("https://")) {
                    mediaUrl = externalUrl;
                    String lower = externalUrl.toLowerCase();
                    if (lower.matches(".*\\.(jpg|jpeg|png|gif|webp)(\\?.*)?")) {
                        attachmentType = AttachmentType.IMAGE;
                    } else if (lower.matches(".*\\.(mp4|webm)(\\?.*)?")) {
                        attachmentType = AttachmentType.VIDEO;
                    } else {
                        attachmentType = AttachmentType.LINK;
                    }
                }

                try {
                    Part filePart = req.getPart("mediaFile");
                    if (filePart != null && filePart.getSize() > 0) {
                        String savedUrl = savedUploadFile(filePart, req, resp);
                        if (savedUrl != null) {
                            mediaUrl = savedUrl;
                            String ext = savedUrl.substring(savedUrl.lastIndexOf('.') + 1).toLowerCase();
                            attachmentType = AUTHORIZED_VIDEO_EXTENSIONS.contains(ext)
                                    ? AttachmentType.VIDEO
                                    : AttachmentType.IMAGE;
                        }
                    }
                } catch (Exception e) {
                    req.setAttribute("error", "Erreur lors de l'upload.");
                    req.getRequestDispatcher("/feeds.jsp").forward(req, resp);
                    return;
                }

                postList.add(new Post(
                        System.currentTimeMillis(),
                        owner.getId(),
                        owner.getUsername(),
                        0L,
                        newPost.trim(),
                        mediaUrl,
                        attachmentType));
                Collections.sort(postList, Comparator.reverseOrder());
            }
        } else if (newComment != null && !newComment.trim().isEmpty() && postIdStr != null) {
            try {
                long postId = Long.parseLong(postIdStr);
                if (owner != null) {
                    Post post = findPostById(postId);
                    if (post != null) {
                        post.addComment(owner.getId(), owner.getUsername(), newComment.trim());
                    }
                }
            } catch (NumberFormatException e) {
                resp.sendError(400, "ID invalide.");
                return;
            }
        }

        else if (buttonLike != null) {
            try {
                long likePostId = Long.parseLong(buttonLike);
                User currentUser = findByUserName(username, users);

                if (currentUser != null) {
                    for (Post post : postList) {
                        if (post.getId() == likePostId) {
                            post.toggleLike(currentUser.getId());
                            break;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                resp.sendError(400, "ID invalide.");
                return;
            }

        }
        resp.sendRedirect(req.getContextPath() + "/feeds");
    }

    private Post findPostById(long postId) {
        for (Post post : postList) {
            if (post.getId() == postId) {
                return post;
            }
        }
        return null;
    }

    private User findByUserName(String username, List<User> users) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private String savedUploadFile(Part filePart, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String originalName = filePart.getSubmittedFileName();

        if (originalName == null || originalName.isBlank()) {
            return null;
        }

        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase()
                : "";

        if (!AUTHORIZED_IMAGE_EXTENSIONS.contains(extension) && !AUTHORIZED_VIDEO_EXTENSIONS.contains(extension)) {
            return null;
        }

        String safeName = UUID.randomUUID().toString() + "." + extension;
        String uploadDir = getServletContext().getRealPath("/uploads");
        File dir = new File(uploadDir);

        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Impossible de créer le dossier d'upload : " + uploadDir);
        }

        filePart.write(uploadDir + File.separator + safeName);

        System.out.println("Fichier sauvegardé : " + uploadDir + File.separator + safeName);
        System.out.println("URL generee : " + req.getContextPath() + "/uploads/" + safeName);
        String url = req.getContextPath() + "/uploads/" + safeName;
        return url;
    }

}
