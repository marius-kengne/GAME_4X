<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Erreur</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            text-align: center;
            padding: 50px;
        }
        .error-container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 15px rgba(0, 0, 0, 0.2);
            display: inline-block;
        }
        h1 {
            color: red;
            font-size: 24px;
        }
        p {
            font-size: 18px;
            margin: 20px 0;
        }
        .btn-home {
            background-color: #4CAF50;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
            text-decoration: none;
        }
        .btn-home:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <h1>Une erreur est survenue</h1>
        <!-- Affichage du message d'erreur passé depuis le contrôleur -->
        <p>
            <%= request.getAttribute("error") != null ? request.getAttribute("error") : "Une erreur inattendue s'est produite. Veuillez réessayer plus tard." %>
        </p>
        <!-- Lien de retour à la page d'accueil -->
        <a href="<%= request.getContextPath() %>/game.jsp" class="btn-home">Retour au jeu</a>
    </div>
</body>
</html>