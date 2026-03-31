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
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --bg:      #0d0d0f;
            --surface: #16161a;
            --border:  #2a2a32;
            --accent:  #c8a96e;
            --accent2: #e8c98e;
            --text:    #e8e6e0;
            --muted:   #6b6870;
            --danger:  #c0675a;
        }

        body {
            background: var(--bg);
            color: var(--text);
            font-family: 'DM Sans', sans-serif;
            min-height: 100vh;
        }

        body::before {
            content: '';
            position: fixed;
            inset: 0;
            background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
            pointer-events: none;
            z-index: 0;
        }

        .orb {
            position: fixed;
            width: 600px; height: 600px;
            border-radius: 50%;
            background: radial-gradient(circle, rgba(200,169,110,0.06) 0%, transparent 70%);
            top: 30%; left: 50%;
            transform: translate(-50%, -50%);
            pointer-events: none;
            z-index: 0;
        }

        nav {
            position: fixed;
            top: 0; left: 0; right: 0;
            z-index: 100;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 1.25rem 3rem;
            border-bottom: 1px solid var(--border);
            background: rgba(13,13,15,0.85);
            backdrop-filter: blur(12px);
        }

        .nav-logo {
            font-family: 'Playfair Display', serif;
            font-size: 1.4rem;
            color: var(--accent);
            letter-spacing: 0.05em;
            text-decoration: none;
        }

        .nav-links {
            display: flex;
            gap: 0.5rem;
            list-style: none;
        }

        .nav-links a {
            font-size: 0.8rem;
            font-weight: 500;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            text-decoration: none;
            color: var(--muted);
            padding: 0.5rem 1rem;
            border-radius: 6px;
            transition: color 0.2s, background 0.2s;
        }

        .nav-links a:hover { color: var(--text); background: var(--border); }
        .nav-links a.logout { color: var(--danger); }
        .nav-links a.logout:hover { background: rgba(192,103,90,0.1); }

        main {
            max-width: 680px;
            margin: 0 auto;
            padding: 7rem 1.5rem 4rem;
            position: relative;
            z-index: 1;
        }

        .compose {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 14px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            animation: fadeUp 0.6s ease both;
        }

        .compose-label {
            font-size: 0.7rem;
            font-weight: 500;
            letter-spacing: 0.15em;
            text-transform: uppercase;
            color: var(--accent);
            margin-bottom: 0.75rem;
        }

        textarea {
            width: 100%;
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 0.85rem 1rem;
            color: var(--text);
            font-family: 'DM Sans', sans-serif;
            font-size: 0.9rem;
            resize: vertical;
            min-height: 90px;
            outline: none;
            transition: border-color 0.2s, box-shadow 0.2s;
        }

        textarea::placeholder { color: var(--muted); }
        textarea:focus {
            border-color: var(--accent);
            box-shadow: 0 0 0 3px rgba(200,169,110,0.1);
        }

        .compose-footer {
            display: flex;
            justify-content: flex-end;
            margin-top: 0.75rem;
        }

        .btn-primary {
            padding: 0.6rem 1.5rem;
            background: var(--accent);
            color: #0d0d0f;
            font-family: 'DM Sans', sans-serif;
            font-size: 0.8rem;
            font-weight: 600;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            transition: background 0.2s, transform 0.1s;
        }

        .btn-primary:hover { background: var(--accent2); }
        .btn-primary:active { transform: scale(0.97); }

        .feed-tabs {
            display: flex;
            margin-bottom: 1.5rem;
            border: 1px solid var(--border);
            border-radius: 10px;
            overflow: hidden;
            list-style: none;
            animation: fadeUp 0.6s ease 0.1s both;
        }

        .feed-tabs li { flex: 1; }

        .feed-tabs a {
            display: block;
            text-align: center;
            padding: 0.7rem;
            font-size: 0.78rem;
            font-weight: 500;
            letter-spacing: 0.1em;
            text-transform: uppercase;
            text-decoration: none;
            color: var(--muted);
            background: var(--surface);
            transition: color 0.2s, background 0.2s;
        }

        .feed-tabs li:first-child a { border-right: 1px solid var(--border); }
        .feed-tabs a:hover { color: var(--text); background: var(--border); }
        .feed-tabs a.active { color: var(--accent); background: rgba(200,169,110,0.08); }

        .post-list {
            display: flex;
            flex-direction: column;
            gap: 1rem;
            list-style: none;
        }

        article {
            background: var(--surface);
            border: 1px solid var(--border);
            border-radius: 14px;
            padding: 1.5rem;
            animation: fadeUp 0.5s ease both;
            transition: border-color 0.2s;
        }

        article:hover { border-color: rgba(200,169,110,0.3); }

        article > header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            margin-bottom: 1rem;
            padding-bottom: 1rem;
            border-bottom: 1px solid var(--border);
        }

        .post-owner {
            font-size: 0.78rem;
            font-weight: 500;
            color: var(--accent);
        }

        .post-date {
            font-size: 0.72rem;
            color: var(--muted);
        }

        .post-content {
            font-size: 0.92rem;
            line-height: 1.65;
            margin-bottom: 1.25rem;
        }

        .post-media {
            margin-bottom: 1.25rem;
            border-radius: 10px;
            overflow: hidden;
            border: 1px solid var(--border);
        }

        .media-img,
        .media-video {
            width: 100%;
            display: block;
            max-height: 400px;
            object-fit: cover;
        }

        .media-link {
            display: block;
            padding: 0.85rem 1rem;
            color: var(--accent);
            font-size: 0.85rem;
            text-decoration: none;
            word-break: break-all;
            transition: background 0.2s;
        }

        .media-link:hover { background: rgba(200,169,110,0.08); }

        article > footer {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 1.5rem;
            padding-bottom: 1.5rem;
            border-bottom: 1px solid var(--border);
        }
        .btn-follow {
            background: var(--accent);
            color: #0d0d0f;
            border: none;
            border-radius: 6px;
            padding: 0.35rem 0.85rem;
            font-size: 0.7rem;
            font-weight: 600;
            text-transform: uppercase;
            cursor: pointer;
            transition: background 0.2s, transform 0.1s;
        }

        .btn-follow:hover { background: var(--accent2); }
        .btn-follow:active { transform: scale(0.95); }
        .btn-like {
            background: none;
            border: 1px solid var(--border);
            border-radius: 999px;
            color: var(--muted);
            font-size: 0.9rem;
            padding: 0.3rem 0.85rem;
            cursor: pointer;
            transition: color 0.2s, border-color 0.2s, background 0.2s, transform 0.1s;
        }

        .btn-like:hover { color: var(--danger); border-color: var(--danger); background: rgba(192,103,90,0.08); }
        .btn-like.liked { color: var(--danger); border-color: var(--danger); background: rgba(192,103,90,0.15); }
        .btn-like.liked:hover { background: rgba(192,103,90,0.25); transform: scale(1.05); }

        .comments-section {
            display: flex;
            flex-direction: column;
            gap: 0.75rem;
        }

        .comments-list {
            display: flex;
            flex-direction: column;
            gap: 0.75rem;
            margin-bottom: 1rem;
            padding: 0.75rem;
            background: rgba(255,255,255,0.02);
            border-left: 2px solid var(--accent);
            border-radius: 4px;
            list-style: none;
        }

        .comment-meta {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            margin-bottom: 0.25rem;
        }

        .comment-author { font-weight: 600; font-size: 0.8rem; color: var(--accent); }
        .comment-time   { font-size: 0.7rem; color: var(--muted); }
        .comment-text   { font-size: 0.85rem; line-height: 1.4; }

        .comment-form {
            display: flex;
            gap: 0.5rem;
            flex-wrap: wrap;
        }

        .comment-input {
            flex: 1;
            min-width: 200px;
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 0.5rem 0.75rem;
            color: var(--text);
            font-family: 'DM Sans', sans-serif;
            font-size: 0.85rem;
            outline: none;
            transition: border-color 0.2s;
        }

        .comment-input::placeholder { color: var(--muted); }
        .comment-input:focus { border-color: var(--accent); box-shadow: 0 0 0 2px rgba(200,169,110,0.1); }

        .btn-comment {
            padding: 0.5rem 1rem;
            background: var(--accent);
            color: #0d0d0f;
            font-family: 'DM Sans', sans-serif;
            font-size: 0.75rem;
            font-weight: 600;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            transition: background 0.2s;
            white-space: nowrap;
        }

        .btn-comment:hover { background: var(--accent2); }

        .media-options {
            margin-top: 0.75rem;
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }

        .media-label {
            font-size: 0.72rem;
            font-weight: 500;
            letter-spacing: 0.1em;
            text-transform: uppercase;
            color: var(--muted);
        }

        .media-input {
            background: var(--bg);
            border: 1px solid var(--border);
            border-radius: 8px;
            padding: 0.6rem 0.85rem;
            color: var(--text);
            font-family: 'DM Sans', sans-serif;
            font-size: 0.85rem;
            outline: none;
            transition: border-color 0.2s;
            width: 100%;
        }

        .media-input:focus { border-color: var(--accent); box-shadow: 0 0 0 2px rgba(200,169,110,0.1); }

        .empty {
            text-align: center;
            padding: 4rem 2rem;
            color: var(--muted);
            font-size: 0.9rem;
        }

        .empty-icon { font-size: 2.5rem; margin-bottom: 1rem; opacity: 0.4; display: block; }

        .sr-only {
            position: absolute; width: 1px; height: 1px;
            padding: 0; overflow: hidden;
            clip: rect(0,0,0,0); white-space: nowrap; border: 0;
        }

        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(16px); }
            to   { opacity: 1; transform: translateY(0); }
        }
    </style>
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