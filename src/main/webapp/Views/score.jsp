<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <title>Mon Score</title>
  <style>
    body {
      margin: 0;
      font-family: Arial, sans-serif;
      background-color: #f7f7f7;
      display: flex;
      flex-direction: column;
      align-items: center;
    }
    .navbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background-color: #28a745; /* Vert pour la navbar */
      color: white;
      padding: 10px 20px;
      width: 100%;
      box-sizing: border-box;
    }
    .navbar h1 {
      margin: 0;
    }
    .navbar .user-info {
      font-size: 14px;
    }
    .container {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      width: 100%;
    }
    .score-container {
      margin-top: 20px;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 10px;
      background-color: #fff;
      text-align: center;
      width: 400px;
    }
    .score {
      font-size: 18px;
      font-weight: bold;
      color: #007bff;
      margin: 10px 0;
    }
    .message {
      margin-top: 10px;
      font-size: 14px;
      color: green;
    }
    .erreur {
      margin-top: 10px;
      font-size: 14px;
      color: red;
    }
    button {
      padding: 10px 15px;
      font-size: 14px;
      font-weight: bold;
      color: white;
      background-color: #28a745; /* Vert */
      border: none;
      border-radius: 5px;
      cursor: pointer;
      margin-top: 20px;
      transition: background-color 0.3s ease;
    }
    button:hover {
      background-color: #218838; /* Vert foncé au survol */
    }
    button:active {
      background-color: #1e7e34; /* Vert encore plus foncé */
    }
    a {
      color: white;
      text-decoration: underline;
    }
  </style>
</head>
<body>

<!-- Navbar -->
<div class="navbar">
  <h1>4X Game - Score</h1>
  <div class="user-info">
    Bienvenue, ${joueur.login} |
    <a href="logout">Déconnexion</a>
  </div>
</div>

<!-- Main Container -->
<div class="container">
  <div class="score-container">
    <h2>Détails du Score</h2>
    <c:if test="${not empty score && not empty territoires && not empty nombreSoldat}">
      <p class="score">Territoires contrôlés : ${territoires}</p>
      <p class="score">Soldats possédés : ${nombreSoldat}</p>
      <p class="score">Score Total : ${score}</p>
    </c:if>
    <c:if test="${not empty erreur}">
      <p class="erreur">${erreur}</p>
    </c:if>
    <c:if test="${empty score && empty erreur}">
      <p class="message">Votre score est en cours de calcul...</p>
    </c:if>
    <button onclick="window.location.href='game'">Retour à l'accueil</button>
  </div>
</div>

</body>
</html>
