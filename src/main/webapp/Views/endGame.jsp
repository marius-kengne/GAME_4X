<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <title>Résultats de la Partie</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f7f7f7;
      text-align: center;
    }
    .message-container {
      margin-top: 50px;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 10px;
      background-color: #fff;
      display: inline-block;
    }
    .message {
      font-size: 18px;
      font-weight: bold;
      color: green;
    }
    .erreur {
      font-size: 18px;
      font-weight: bold;
      color: red;
    }
  </style>
</head>
<body>
<div class="message-container">
  <h2>Résultat de la Partie</h2>
  <c:if test="${not empty flashSuccess}">
    <p class="message">${flashSuccess}</p>
  </c:if>
  <c:if test="${not empty flashErreur}">
    <p class="erreur">${flashErreur}</p>
  </c:if>
</div>
</body>
</html>
