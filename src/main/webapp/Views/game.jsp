<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.projet.game_4x.models.*" %>
<%
    // Récupérer les données de session
    Joueur joueur = (Joueur) session.getAttribute("joueur");
    Carte carte = (Carte) request.getAttribute("carte"); // Injectée par le contrôleur
    int tourActuel = (int) request.getAttribute("tourActuel");
%>
<!DOCTYPE html>
<html>
<head>
    <title>4X Game - Plateau</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            margin: 0;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }
        table {
            border-collapse: collapse;
            margin: 20px auto;
            background-color: #fff;
        }
        td {
            width: 50px;
            height: 50px;
            text-align: center;
            border: 1px solid black;
        }
        img {
            width: 40px;
            height: 40px;
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
        .popup {
            min-width: 450px;
            min-height: 150px;
            display: none;
            position: fixed;
            top: 28%;
            left: 50%;
            transform: translate(-50%, -50%);
            background-color: white;
            border: 1px solid #ccc;
            padding: 20px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.2);
            z-index: 1000;
        }
        .popup-overlay {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
            z-index: 999;
        }
        .close-btn {
            padding: 5px 10px;
            background-color: red;
            color: white;
            border: none;
            cursor: pointer;
        }
        #actions {
            margin-top: 10px;
        }

        .soldat-joueur {
            background-color: green;
        }
        .soldat-adversaire {
            background-color: red;
        }
    </style>
    <script>
        function showPopup() {
            document.getElementById("popup").style.display = "block";
            document.getElementById("popup-overlay").style.display = "block";
        }
        function closePopup() {
            document.getElementById("popup").style.display = "none";
            document.getElementById("popup-overlay").style.display = "none";
        }
    </script>
</head>
<body>

<h1>Bienvenue, ${joueur.login} !</h1>
<button onclick="showPopup()">Voir les informations du tour</button>

<!-- Pop-up -->
<div id="popup-overlay" class="popup-overlay" onclick="closePopup()"></div>
<div id="popup" class="popup">
    <h3>Tour actuel : ${tourActuel}</h3>
    <h3>Points de production : ${joueur.pointsProduction}</h3>
    <button class="close-btn" onclick="closePopup()">Fermer</button>
</div>

<!-- Grille de la carte -->
<div id="game-board">
    <table>
        <c:forEach var="y" begin="0" end="${carte.hauteur - 1}">
            <tr>
                <c:forEach var="x" begin="0" end="${carte.largeur - 1}">
                    <td>
                        <c:choose>
                            <c:when test="${carte.getTuile(x, y).type == 'montagne'}">
                                <img src="resources/icons/Large/mountain.png" alt="Montagne">
                            </c:when>
                            <c:when test="${carte.getTuile(x, y).type == 'foret'}">
                                <img src="resources/icons/Large/forest.png" alt="Forêt">
                            </c:when>
                            <c:when test="${carte.getTuile(x, y).type == 'ville'}">
                                <img src="resources/icons/Large/city.png" alt="Ville">
                            </c:when>
                            <c:otherwise>
                                <c:if test="${carte.getTuile(x, y).soldat != null}">
                                    <c:choose>
                                        <c:when test="${carte.getTuile(x, y).soldat.proprietaire.id == joueur.id}">
                                            <div class="soldat-joueur">
                                                <img src="resources/icons/Large/soldier.png" alt="Ville">
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="soldat-adversaire">
                                                <img src="resources/icons/Large/soldier.png" alt="Ville">
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </c:forEach>
            </tr>
        </c:forEach>
    </table>
</div>

<!-- Actions disponibles -->
<div id="actions">
    <form action="actions" method="post" style="display: flex; gap: 10px; justify-content: center; align-items: center;">
        <button type="submit" name="action" value="moveNorth">Move North</button>
        <button type="submit" name="action" value="moveSouth">Move South</button>
        <button type="submit" name="action" value="moveEast">Move East</button>
        <button type="submit" name="action" value="moveWest">Move West</button>
        <button type="submit" name="action" value="heal">Heal</button>
        <button type="submit" name="action" value="forage">Forage</button>
        <button type="submit" name="action" value="endTurn">End Turn</button>
    </form>
</div>

</body>
</html>