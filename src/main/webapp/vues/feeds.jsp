<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="fr.simplon.domain.models.*" %>
<%@ page import="fr.simplon.domain.repository.PostRepositoryInterface" %>
<%@ page import="fr.simplon.infrastructure.repository.PostRepository" %>


<%
    List<Post> postList = (List<Post>) request.getAttribute("postList");
    PostRepositoryInterface postRepository = new PostRepository();
    String feedType = (String) request.getAttribute("feedType");
    if (feedType == null) feedType = "recommendations";
    java.time.format.DateTimeFormatter fmt =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Fil — Miniature</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/feeds.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/marked/9.1.6/marked.min.js"></script>
</head>
<body>

<nav>
    <a class="nav-logo" href="${pageContext.request.contextPath}/feeds">Miniature</a>
    <ul class="nav-links">
        <li><a href="${pageContext.request.contextPath}/home">Accueil</a></li>
        <li><a href="${pageContext.request.contextPath}/logout" class="logout">Se déconnecter</a></li>
    </ul>
</nav>

<main>

    <section class="compose">
        <p class="compose-label">Nouveau post</p>
        <form method="post" action="${pageContext.request.contextPath}/feeds" enctype="multipart/form-data">
            <textarea name="newPost" placeholder="Quoi de neuf ?"></textarea>
            <div class="media-options">
                <label class="media-label" for="mediaFile">Fichier (image ou vidéo)</label>
                <input type="file" id="mediaFile" name="mediaFile"
                       accept="image/*,video/mp4,video/webm" class="media-input">
                <label class="media-label" for="externalUrl">Ou coller un lien externe</label>
                <input type="url" id="externalUrl" name="externalUrl"
                       placeholder="https://..." class="media-input">
            </div>
            <div class="compose-footer">
                <button type="submit" class="btn-primary">Publier</button>
            </div>
        </form>
    </section>

    <ul class="feed-tabs">
        <li>
            <a href="${pageContext.request.contextPath}/feeds?type=recommendations"
               class="<%= "recommendations".equals(feedType) ? "active" : "" %>">
                Recommandations
            </a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/feeds?type=subscriptions"
               class="<%= "subscriptions".equals(feedType) ? "active" : "" %>">
                Abonnements
            </a>
        </li>
    </ul>

    <% if (postList == null || postList.isEmpty()) { %>
    <p class="empty">
        <span class="empty-icon" aria-hidden="true">✦</span>
        Aucun post pour le moment.<br>Soyez le premier à publier !
    </p>
    <% } else { %>
    <ul class="post-list">
        <% for (Post post : postList) { %>
        <li>
            <article>

                <header>
                    <span class="post-owner">@ <%= post.getOwnerUsername() %></span>
                    <time class="post-date" datetime="<%= post.getCreatedAt() %>">
                        <%= post.getCreatedAt().format(fmt) %>
                    </time>
                </header>

                <p class="post-content" data-markdown="<%= post.getContent().replace("\"", "&quot;").replace("<", "&lt;") %>"></p>

                <% if (post.hasMedia()) { %>
                <div class="post-media">
                    <% if (post.getAttachmentType() == AttachmentType.IMAGE) { %>
                    <img src="<%= post.getMediaUrl() %>"
                         alt="Image partagée par <%= post.getOwnerUsername() %>"
                         class="media-img">
                    <% } else if (post.getAttachmentType() == AttachmentType.VIDEO) { %>
                    <video controls muted class="media-video">
                        <source src="<%= post.getMediaUrl() %>">
                    </video>
                    <% } else if (post.getAttachmentType() == AttachmentType.LINK) { %>
                    <a href="<%= post.getMediaUrl() %>"
                       target="_blank"
                       rel="noopener noreferrer"
                       class="media-link">
                        🔗 <%= post.getMediaUrl() %>
                    </a>
                    <% } %>
                </div>
                <% } %>

                <%
                    Long currentUserId = (Long) request.getAttribute("currentUserId");
                    long uid = currentUserId != null ? currentUserId : -1L;
                %>

                <footer>
                    <form method="post" action="<%= request.getContextPath() %>/feeds">
                        <input type="hidden" name="buttonLike" value="<%= post.getId() %>">
                        <button type="submit" class="btn-like <%= postRepository.isLikedBy(uid) ? "liked" : "" %>">
                            ❤ <span><%= postRepository.getLikeCount() %></span>
                        </button>
                    </form>
                </footer>

                <section class="comments-section">
                    <% List<Comment> comments = post.getComments(); %>
                    <% if (comments != null && !comments.isEmpty()) { %>
                    <ul class="comments-list">
                        <% for (Comment comment : comments) { %>
                        <li>
                            <div class="comment-meta">
                                <span class="comment-author">@ <%= comment.getAuthorUsername() %></span>
                                <%
                                    LocalDateTime createdAt = comment.getCreatedAt();
                                    if (createdAt != null) {
                                %>
                                <time class="comment-time" datetime="<%= createdAt.format(DateTimeFormatter.ISO_DATE_TIME) %>">
                                    <%= createdAt.format(fmt) %>
                                </time>
                                <% } %>
                            </div>
                            <p class="comment-text"><%= comment.getContent() %></p>
                        </li>
                        <% } %>
                    </ul>
                    <% } %>

                    <form method="post" action="${pageContext.request.contextPath}/feeds"
                          class="comment-form">
                        <input type="hidden" name="postId" value="<%= post.getId() %>">

                        <label for="comment-<%= post.getId() %>" class="sr-only">Commentaire</label>
                        <input type="text"
                               id="comment-<%= post.getId() %>"
                               name="newComment"
                               placeholder="Ajouter un commentaire..."
                               class="comment-input">
                        <button type="submit" class="btn-comment">Envoyer</button>
                    </form>
                </section>

            </article>
        </li>
        <% } %>
    </ul>
    <% } %>

</main>
<script>
    document.querySelectorAll('[data-markdown]').forEach(el => {
        el.innerHTML = marked.parse(el.dataset.markdown);
    });
</script>

</body>
</html>