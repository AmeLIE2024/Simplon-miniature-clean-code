<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Date" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<% String user = request.getParameter("user"); Date date = new Date(); %>

<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Accueil - Miniature</title>
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;700&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/global.css">
</head>
<body>

<div class="orb"></div>

<nav>
  <a class="nav-logo" href="${pageContext.request.contextPath}/feeds">Miniature</a>
  <div class="nav-links">
    <%
      HttpSession currentSession = request.getSession(false);
      boolean isLogged = currentSession != null && currentSession.getAttribute("loggedUser") != null;
    %>

    <% if (!isLogged) { %>
    <a href="${pageContext.request.contextPath}/login">Se connecter</a>
    <a href="${pageContext.request.contextPath}/register">S'inscrire</a>
    <% } else { %>
    <a href="${pageContext.request.contextPath}/logout" class="logout">Se déconnecter</a>
    <% } %>
  </div>
</nav>

<main>
  <div class="hero">
    <div class="hero-eyebrow">Tableau de bord</div>
    <h1>Bienvenue <span>${loggedUser}</span>&nbsp;!</h1>
    <p class="hero-sub">Vous êtes connecté à votre espace personnel.<br>Retrouvez ici toutes vos ressources.</p>
    <div class="date-badge"><%=date%></div>
  </div>
</main>

<footer>© 2025 Miniature — Tous droits réservés</footer>

</body>
</html>

