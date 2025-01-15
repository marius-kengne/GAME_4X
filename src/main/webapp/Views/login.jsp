<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <style>
        /* Style général pour centrer le contenu */
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #28a745;
        }

        .container {
            background-color: white;
            padding: 20px 30px;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            width: 100%;
            max-width: 400px;
            box-sizing: border-box;
        }

        h1 {
            text-align: center;
            margin-bottom: 20px;
            font-size: 24px;
            color: #333;
        }

        label {
            display: block;
            font-weight: bold;
            margin-bottom: 5px;
            color: #555;
        }

        input[type="text"], input[type="password"] {
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            width: 100%;
            box-sizing: border-box;
            margin-bottom: 15px;
        }

        button[type="submit"] {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 10px;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            width: 100%;
            transition: background-color 0.3s;
        }

        button[type="submit"]:hover {
            background-color: #45a049;
        }

        p.error-message, p.success-message {
            text-align: center;
            margin-top: 10px;
            font-size: 14px;
        }

        p.error-message {
            color: red;
        }

        p.success-message {
            color: green;
        }

        .toggle-link {
            display: block;
            text-align: center;
            margin-top: 10px;
            font-size: 14px;
            color: #007bff;
            text-decoration: none;
            cursor: pointer;
        }

        .toggle-link:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>

<div class="container">
    <h1> Authentification </h1>

    <!-- Affichage des messages -->
    <%
        String errorMessage = (String) request.getAttribute("erreur");
        String successMessage = (String) session.getAttribute("success");
    %>
    <% if (errorMessage != null) { %>
    <p class="error-message"><%= errorMessage %></p>
    <% } %>
    <% if (successMessage != null) { %>
    <p class="success-message"><%= successMessage %></p>
    <% } %>

    <!-- Formulaire de connexion -->
    <form action="login" method="POST">
        <input type="hidden" name="action" value="login">
        <label for="login">Login :</label>
        <input type="text" id="login" name="login" required>

        <label for="password">Mot de Passe :</label>
        <input type="password" id="password" name="password" required>

        <button type="submit">Se connecter</button>
    </form>

    <!-- Lien pour basculer vers l'inscription -->
    <a class="toggle-link" href="/game_4x/register">Créer un compte</a>
</div>
</body>
</html>
