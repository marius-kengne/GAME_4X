<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Inscription</title>
    <style>
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
        }

        h1 {
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }

        input[type="text"], input[type="password"], select {
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            width: 100%;
            margin-bottom: 15px;
        }

        button[type="submit"] {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 10px;
            border-radius: 4px;
            width: 100%;
            font-weight: bold;
        }

        button[type="submit"]:hover {
            background-color: #45a049;
        }

        p.error-message {
            text-align: center;
            color: red;
        }
    </style>
</head>
<body>
<form action="${pageContext.request.contextPath}/inscription" method="POST">
    <h1> Inscription </h1>

    <!-- Affichage des erreurs -->
    <% if (request.getAttribute("error") != null) { %>
    <p class="error-message"><%= request.getAttribute("error") %></p>
    <% } %>

    <label for="login">Login :</label>
    <input type="text" id="login" name="login" required>

    <label for="email">Email :</label>
    <input type="text" name="email" required>

    <label for="password">Mot de Passe :</label>
    <input type="password" id="password" name="mot_de_passe" required>

    <button type="submit">S'inscrire</button>
</form>
</body>
</html>
