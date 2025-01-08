<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.projet.game_4x.models.*" %>
<%
    // Récupérer les données de session
    Joueur joueur = (Joueur) session.getAttribute("joueur");
    Carte carte = (Carte) request.getAttribute("carte"); // Injectée par le contrôleur
    int tourActuel = (int) session.getAttribute("tourActuel");
    String playerId = (String) joueur.getLogin();
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
        function showPopupWebsocket(message) {
            document.getElementById("popup-websocket").style.display = "block";
            document.getElementById("popup-overlay").style.display = "block";
            document.getElementById("message-error").innerText = message;
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

<!-- Pop-up erreur websocket-->
<div id="popup-overlay" class="popup-overlay" onclick="closePopup()"></div>
<div id="popup-websocket" class="popup">
    <h3 id="message-error"></h3>
    <button class="close-btn" onclick="closePopup()">Fermer</button>
</div>

<!-- Affichage des messages -->
<%
    String errorMessage = (String) request.getAttribute("erreur");
    String successMessage = (String) session.getAttribute("message");
%>
<% if (errorMessage != null) { %>
<p class="error-message"><%= errorMessage %></p>
<% } %>
<% if (successMessage != null) { %>
<p class="success-message"><%= successMessage %></p>
<% } %>

<!-- Grille de la carte -->
<div id="game-board">
    <c:if test="${carte != null}">
        <table>
            <c:forEach var="y" begin="0" end="${carte.hauteur - 1}">
                <tr>
                    <c:forEach var="x" begin="0" end="${carte.largeur - 1}">
                        <td>
                            <c:choose>
                                <c:when test="${carte.getTuile(x, y) != null && carte.getTuile(x, y).type == 'montagne'}">
                                    <img src="resources/icons/Large/mountain.png" alt="Montagne">
                                </c:when>
                                <c:when test="${carte.getTuile(x, y) != null && carte.getTuile(x, y).type == 'foret'}">
                                    <img src="resources/icons/Large/forest.png" alt="Forêt">
                                </c:when>
                                <c:when test="${carte.getTuile(x, y) != null && carte.getTuile(x, y).type == 'ville'}">
                                    <img src="resources/icons/Large/city.png" alt="Ville">
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${carte.getTuile(x, y) != null && carte.getTuile(x, y).soldat != null && carte.getTuile(x, y).soldat.proprietaire.id == joueur.id}">
                                            <div class="soldat-joueur">
                                                <img src="resources/icons/Large/soldier.png" alt="Soldat joueur">
                                            </div>
                                        </c:when>
                                        <c:when test="${carte.getTuile(x, y) != null && carte.getTuile(x, y).soldat != null}">
                                            <div class="soldat-adversaire">
                                                <img src="resources/icons/Large/soldier.png" alt="Soldat adversaire">
                                            </div>
                                        </c:when>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</div>


<!-- Actions disponibles -->
<!--
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
-->

<div id="actions">
    <form id="deplacementForm" action="deplacerSoldat" method="post" style="display: flex; gap: 10px; justify-content: center; align-items: center;">
        <label for="soldat">Choisir un soldat :</label>
        <select name="soldatId" id="soldat" required>
            <c:forEach var="soldat" items="${joueur.soldats}">
                <option value="${soldat.id}">
                    Soldat (${soldat.position.x}, ${soldat.position.y})
                </option>
            </c:forEach>
        </select>

        <!-- Champ caché pour stocker la direction -->
        <input type="hidden" name="direction" id="directionInput">

        <!-- Boutons pour les directions -->
        <button type="button" onclick="submitDirection('moveNorth')">↑</button>
        <button type="button" onclick="submitDirection('moveSouth')">↓</button>
        <button type="button" onclick="submitDirection('moveEast')">→</button>
        <button type="button" onclick="submitDirection('moveWest')">←</button>
    </form>
</div>

<script>
    let lastSubmitted = null; // Garde en mémoire la dernière soumission pour éviter les doublons

    function submitDirection(direction) {
        const now = new Date().getTime();

        // Empêche les soumissions répétées rapides
        if (lastSubmitted && now - lastSubmitted < 500) {
            console.warn("Double soumission évitée.");
            return;
        }

        lastSubmitted = now;

        // Définir la direction choisie dans le champ caché
        document.getElementById('directionInput').value = direction;
        // Soumettre le formulaire
        document.getElementById('deplacementForm').submit();
    }
</script>



<script>

    // Connecter au WebSocket
    //const playerId = ${joueur.login}; // Id unique du joueur
    const socket = new WebSocket(`ws://172.20.10.13:8082/game_4x/gameUpdates/${joueur.login}`);

    // Quand une connexion est ouverte
    socket.onopen = function () {
        console.log("Connexion WebSocket ouverte.");
    };

    // Quand un message est reçu
    socket.onmessage = function (event) {
        const message = event.data;
        // Mettez à jour dynamiquement la page en fonction du message
        if (message.includes("Le soldat a été déplacé")) {
            location.reload(); // Exemple simple : recharger la page
        }
        console.log("Message reçu : " + message);
    };

    // Quand une connexion est fermée
    socket.onclose = function () {
        console.log("Connexion WebSocket fermée.");
    };
</script>

</body>
</html>