<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Inscription - Miniature</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/register.css">
</head>
<body>

<div class="orb"></div>

<div class="card">
    <div class="card-eyebrow">Miniature</div>
    <h1>Inscription</h1>
    <p class="subtitle">Créez votre espace personnel</p>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert-error">✕ ${error}</div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/register">

        <div class="form-row">
            <div class="form-group">
                <label for="username">Nom d'utilisateur</label>
                <input type="text" id="username" name="username" placeholder="votre_pseudo" required />
            </div>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" placeholder="you@mail.com" required />
            </div>
        </div>

        <div class="divider">Sécurité</div>

        <div class="form-group">
            <label for="password">Mot de passe</label>
            <input type="password" id="password" name="password" placeholder="••••••••" required />
        </div>

        <div class="form-group">
            <label for="confirmPassword">Confirmation</label>
            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="••••••••" required />
        </div>

        <button type="submit">Créer mon compte</button>
    </form>

    <div class="card-footer">
        <a href="${pageContext.request.contextPath}/login">Déjà un compte ?</a>
        <a href="${pageContext.request.contextPath}/home">Retour à l'accueil</a>
    </div>
</div>

</body>
</html>
