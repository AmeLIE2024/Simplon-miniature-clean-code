<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Connexion - Miniature</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/login.css">
</head>
<body>
<div class="orb"></div>

<div class="card">
    <div class="card-eyebrow">Miniature</div>
    <h1>Connexion</h1>
    <p class="subtitle">Accédez à votre espace personnel</p>

    <% if ("true".equals(request.getParameter("registered"))) { %>
    <div class="alert alert-success">
        ✓ Inscription réussie ! Connectez-vous.
    </div>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-error">
        ✕ ${error}
    </div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="form-group">
            <label for="username">Nom d'utilisateur</label>
            <input type="text" id="username" name="username" placeholder="votre_pseudo" required />
        </div>
        <div class="form-group">
            <label for="password">Mot de passe</label>
            <input type="password" id="password" name="password" placeholder="••••••••" required />
        </div>
        <button type="submit">Se connecter</button>
    </form>

    <div class="card-footer">
        Pas encore de compte ?
        <a href="${pageContext.request.contextPath}/register">S'inscrire</a>
    </div>
</div>

</body>
</html>