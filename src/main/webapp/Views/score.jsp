<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.projet.game_4x.models.Joueur" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Score du joueur</title>
    <style>
        body {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            min-height: 100vh;
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
        }
        .container {
            background-color: white;
            padding: 20px 30px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            text-align: center;
            width: 100%;
            max-width: 400px;
        }
        .btn {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            margin-top: 20px;
        }
        .btn:hover {
            background-color: #45a049;
        }
        .error {
            color: red;
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Score du joueur</h1>
    <%
        // Récupération du joueur depuis la session
        Joueur joueur = (Joueur) session.getAttribute("joueur");
        if (joueur == null) {
    %>
    <p class="error">Vous n'êtes pas connecté. Veuillez vous connecter pour voir votre score.</p>
    <a class="btn" href="${pageContext.request.contextPath}/login">Retour à la page de connexion</a>
    <%
        } else {
    %>
    <h2>Bienvenue, <%= joueur.getLogin() %> !</h2>
    <p>Votre score actuel : <strong><%= joueur.getScore() %></strong></p>

    <!-- Formulaire pour ajouter des points -->
    <form action="${pageContext.request.contextPath}/score" method="post">
        <label for="points">Ajouter des points :</label>
        <input type="number" name="points" id="points" min="1" required>
        <button class="btn" type="submit">Ajouter des points</button>
    </form>

    <!-- Bouton pour retourner au jeu -->
    <form action="${pageContext.request.contextPath}/game" method="get">
        <button class="btn" type="submit">Retour au jeu</button>
    </form>
    <%
        }
    %>
</div>
</body>
</html>