<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Formulaire</title>
    <style>
        /* Style général pour centrer le contenu */
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
        }

        form {
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

        input[type="text"], select {
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

        p.error-message {
            text-align: center;
            color: red;
            margin-top: 10px;
        }
    </style>
</head>
<body>
<form action="LoginController" method="POST">
    <h1> Authentification </h1>

    <!-- Affichage des erreurs -->
    <% String errorMessage = (String) request.getAttribute("erreur"); %>
    <% if (errorMessage != null) { %>
    <p class="error-message"><%= errorMessage %></p>
    <% } %>

    <label for="login">Login :</label>
    <input type="text" id="login" name="login">

    <label for="password">Mot de Passe :</label>
    <input type="text" id="password" name="password">

    <button type="submit">Envoyer</button>
</form>
</body>
</html>
