<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.projet.game_4x.models.*" %>
<%
    // Récupérer les données de session
    Joueur joueur = (Joueur) session.getAttribute("joueur");
    Game game = (Game) application.getAttribute("game");
    /*
    if (game == null) {
        out.println("<h1>Erreur : le jeu n'a pas encore été initialisé !</h1>");
        return;
    }*/
%>
<!DOCTYPE html>
<html>
<head>
    <title>4X Game - Home</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #28a745;
            margin: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100vh;
        }
        h1 {
            margin-bottom: 20px;
            color: #333;
        }
        form {
            display: flex;
            gap: 10px;
            justify-content: center;
            align-items: center;
        }
        button {
            padding: 10px 15px;
            font-size: 14px;
            font-weight: bold;
            color: white;
            background-color: #007bff;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }
        button:hover {
            background-color: #0056b3;
        }
        button:active {
            background-color: #003f7f;
        }
    </style>
</head>
<body>

<h1>Bienvenue, ${joueur.login} !</h1>
<% if (game != null && game.isStart()) { %>
<form action="new_game" method="get">
    <button type="submit">Rejoindre le jeu</button>
</form>
    <% } else { %>
<form action="game" method="get">
    <button type="submit">Commencer le jeu</button>
</form>
    <% } %>


</body>
</html>
