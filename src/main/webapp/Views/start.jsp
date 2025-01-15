<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.projet.game_4x.models.*" %>
<%
    // Récupération des données depuis la session
    Game game = (Game) application.getAttribute("game");
    int playerId = (int) session.getAttribute("playerId");
    //int playerId = (int) game.getCurrentPlayer();
    if (game == null) {
        out.println("<h1>Erreur : le jeu n'a pas encore été initialisé !</h1>");
        return;
    }

    Joueur joueur = (Joueur) session.getAttribute("joueur");

    //int tourActuel = game.getCurrentPlayer();
    int tourActuel = (int) session.getAttribute("tourActuel");
%>
<!DOCTYPE html>
<html>
<head>
    <title>4X Game - Plateau</title>
    <style>
        .soldat-joueur {
            background-color: green;
        }
        .soldat-adversaire {
            background-color: red;
        }
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background-color: #f7f7f7;
            display: flex;
            flex-direction: column;
        }
        .navbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            /*background-color: #007bff;*/
            background-color: #28a745;
            color: white;
            padding: 10px 20px;
        }
        .navbar h1 {
            margin: 0;
        }
        .navbar .user-info {
            font-size: 14px;
        }
        .container {
            display: flex;
            height: calc(100vh - 50px);
        }
        .left-panel, .right-panel {
            width: 20%;
            padding: 20px;
            background-color: #e9ecef;
            box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.1);
            overflow-y: auto;
        }
        .main-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            align-items: center;
            padding: 20px;
        }
        table {
            border-collapse: collapse;
            margin: auto;
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
            display: block;
            width: 100%;
            margin-bottom: 10px;
            padding: 10px;
            font-size: 14px;
            font-weight: bold;
            color: white;
            /*background-color: #007bff;*/
            background-color: #28a745;
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
        .chat-box, .notifications {
            border: 1px solid #ccc;
            border-radius: 5px;
            margin-bottom: 20px;
            padding: 10px;
            background-color: #fff;
        }
        .chat-box {
            height: 300px;
            overflow-y: auto;
        }
        .chat-input {
            display: flex;
            margin-top: 10px;
        }
        .chat-input input {
            flex: 1;
            padding: 5px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .chat-input button {
            flex-shrink: 0;
            padding: 6px 12px;
            font-size: 14px;
        }
        .direction-buttons {
            display: flex;
            justify-content: center;
            gap: 10px;
            margin-top: 20px;
        }
    </style>
</head>
<body>

<!-- Navbar -->
<div class="navbar">
    <h1>4X Game</h1>
    <div class="user-info">
        Bienvenue, ${joueur.login} |
        <a href="logout" style="color: white; text-decoration: underline;">Déconnexion</a>
    </div>
</div>

<!-- Main Container -->
<div class="container">
    <!-- Left Panel: Actions -->
    <div class="left-panel">
        <button onclick="showPopup()">Informations de partie</button>
        <h3>Tour actuel : ${tourActuel}</h3>
        <h3>Points de production : ${joueur.pointsProduction}</h3>
        <br><hr>
        <form id="score" action="score" method="get" style="display: flex; gap: 10px; justify-content: center; align-items: center;">
            <button type="submit">Voir Score</button>
        </form>
        <hr>
        <h3>Actions</h3>
        <form id="actionsForm" action="actions" method="post">
            <input type="hidden" name="direction" id="directionInput1">
            <button type="submit" name="action" value="recruit">Recruter un soldat</button>
            <button type="submit" onclick="submitAction('heal')">Soigner</button>
            <button type="submit" onclick="submitAction('forage')">Chercher des ressources</button>
            <button type="submit" onclick="submitAction('endTurn')">End Turn</button>
            <button type="submit" onclick="submitAction('endGame')">End Game</button>
        </form>
    </div>

    <!-- Main Content: Game Board -->
    <div class="main-content">
        <%
            Integer id = game.getCurrentPlayer(); // Récupérer l'ID du joueur actuel
            Joueur currentPlayer = null;

            if (id != null) {
                try {
                    currentPlayer = Joueur.getJoueurById(id); // Obtenir le joueur actuel
                } catch (Exception e) {

                    out.println("<p style='color: red;'>Erreur : joueur introuvable.</p>");
                }
            }
        %>
        <% if (currentPlayer != null) { %>
        <h2 id="turn-info" style="color: red">C'est au tour de <strong style="font-weight: bold"><%= currentPlayer.getLogin()%></strong> de jouer</h2>
        <% } else { %>
        <p>En attente du tour des autres joueurs...</p>
        <% } %>
        <table>
            <c:forEach var="y" begin="0" end="${game.getCarte().hauteur - 1}">
                <tr>
                    <c:forEach var="x" begin="0" end="${game.getCarte().largeur - 1}">
                        <%
                            int currentX = Integer.parseInt(String.valueOf(pageContext.getAttribute("x")));
                            int currentY = Integer.parseInt(String.valueOf(pageContext.getAttribute("y")));
                        %>
                        <td>
                            <c:choose>
                                <c:when test="${game.getCarte().getTuile(x, y) != null && game.getCarte().getTuile(x, y).type == 'montagne'}">
                                    <img src="resources/icons/Large/mountain.png" alt="Montagne">
                                </c:when>
                                <c:when test="${game.getCarte().getTuile(x, y) != null && game.getCarte().getTuile(x, y).type == 'foret'}">
                                    <img src="resources/icons/Large/forest.png" alt="Forêt">
                                </c:when>
                                <c:when test="${game.getCarte().getTuile(x, y) != null && game.getCarte().getTuile(x, y).type == 'ville'}">
                                    <!--img src="resources/icons/Large/city.png" alt="Ville"-->
                                    <c:choose>
                                        <c:when test="${game.getCarte().getTuile(x, y).getProprietaire() != null && game.getCarte().getTuile(x, y).getProprietaire().getId() == joueur.id}">
                                            <!-- Ville capturée par le joueur -->
                                            <div style="background-color: lightgreen; padding: 5px;">
                                                <img src="resources/icons/Large/city.png" alt="Ville capturée">
                                                <p style="font-size: small;">Capturée</p>
                                            </div>
                                        </c:when>
                                        <c:when test="${game.getCarte().getTuile(x, y).getProprietaire() != null}">
                                            <!-- Ville capturée par un autre joueur -->
                                            <div style="background-color: lightcoral; padding: 5px;">
                                                <img src="resources/icons/Large/city.png" alt="Ville ennemie">
                                                <p style="font-size: small;">Par <%= game.getCarte().getTuile(currentX, currentY).getProprietaire().getLogin() %></p>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <!-- Ville non capturée -->
                                            <img src="resources/icons/Large/city.png" alt="Ville neutre">
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${game.getCarte().getTuile(x, y) != null && game.getCarte().getTuile(x, y).soldat != null && game.getCarte().getTuile(x, y).soldat.proprietaire.id == joueur.id}">
                                            <div class="soldat-joueur">
                                                <img src="resources/icons/Large/soldier.png" alt="Soldat joueur">
                                            </div>
                                        </c:when>
                                        <c:when test="${game.getCarte().getTuile(x, y) != null && game.getCarte().getTuile(x, y).soldat != null}">
                                            <div class="soldat-adversaire">
                                                <img src="resources/icons/Large/soldier.png" alt="Soldat adversaire">
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div style="height: 40px;"></div>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>
        <div id="actions" class="direction-buttons">
            <% if (game.getCurrentPlayer() == joueur.getId()) { %>
            <form id="deplacementForm" action="deplacerSoldat" method="post" style="display: flex; gap: 10px; justify-content: center; align-items: center;">
                <label for="soldat">Soldats </label>
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
                <button onclick="submitDirection('moveNorth')">↑</button>
                <button onclick="submitDirection('moveSouth')">↓</button>
                <button onclick="submitDirection('moveEast')">→</button>
                <button onclick="submitDirection('moveWest')">←</button>
                <button style="background: gainsboro" onclick="submitAction('recruit')">Recruit</button>
                <!--button type="submit" name="action" value="heal">Heal</button>
                <button type="submit" name="action" value="forage">Forage</button-->

            </form>
            <% } else { %>
            <p>En attente du tour des autres joueurs...</p>
            <% } %>
        </div>
    </div>

    <!-- Right Panel: Chat and Notifications -->
    <div class="right-panel">

        <h3>Notifications</h3>
        <div id="notifications" class="notifications">
            <%
                String flashSuccess = (String) session.getAttribute("flashSuccess");
                String flashErreur = (String) session.getAttribute("flashErreur");
                session.removeAttribute("flashSuccess");
                session.removeAttribute("flashErreur");
            %>
            <% if (flashSuccess != null) { %>
            <div class="success-message" style="color: green;">
                <%= flashSuccess %>
            </div>
            <% } %>
            <% if (flashErreur != null) { %>
            <div class="error-message" style="color: red;">
                <%= flashErreur %>
            </div>
            <% } %>
        </div>
        <br><hr>
        <h3>Chat</h3>
        <div class="chat-box" id="chatBox"></div>
        <div class="chat-input">
            <input type="text" id="chatInput" style="width: 200px" placeholder="Tapez votre message...">
            <button style="width: 100px" onclick="sendMessage()">send</button>
        </div>

    </div>
</div>


<script>
    const notificationContainer = document.querySelector('.notifications');

    // Fonction pour afficher une notification
    function showNotification(message, type) {
        const notifications = document.getElementById('notifications');
        notifications.innerHTML = ''; // Vider les notifications existantes

        const notification = document.createElement('div');
        notification.textContent = message;
        notification.style.color = type === 'success' ? 'green' : 'red';
        notification.className = type === 'success' ? 'success-message' : 'error-message';

        notifications.appendChild(notification);

        // Optionnel : Supprimez la notification après quelques secondes
        setTimeout(() => {
            notifications.innerHTML = '';
        }, 5000); // Supprimer après 5 secondes
    }
</script>
<script>
    const chatBox = document.getElementById('chatBox');
    const chatKey = `chatMessages-${joueur.getId()}`; // Clé unique pour chaque joueur
    const socket = new WebSocket(`ws://192.168.1.167:8082/game_4x/gameUpdates/${joueur.login}`);

    // Charger les messages depuis le localStorage
    function loadChatMessages() {
        const savedMessages = localStorage.getItem(chatKey);
        if (savedMessages) {
            const messages = JSON.parse(savedMessages);
            messages.forEach(message => {
                const messageElement = document.createElement('div');
                messageElement.textContent = message;
                chatBox.appendChild(messageElement);
            });
            chatBox.scrollTop = chatBox.scrollHeight;
        }
    }

    // Sauvegarder les messages dans le localStorage
    function saveChatMessage(message) {
        let txt = message.replace("flashErreur", "");
        let txt1 = message.replace("flashSuccess", "");
        let messages = localStorage.getItem(chatKey);
        messages = messages ? JSON.parse(messages) : [];
        messages.push(txt1);
        localStorage.setItem(chatKey, JSON.stringify(messages));
    }

    // Écouter les messages du WebSocket
    socket.onmessage = function (event) {
        const message = event.data;
        const messageElement = document.createElement('div');
        messageElement.textContent = message;
        chatBox.appendChild(messageElement);
        chatBox.scrollTop = chatBox.scrollHeight; // Scroll to the bottom

        saveChatMessage(message); // Sauvegarder dans le localStorage

        if (message.includes("Ville capturée")) {
            refreshPageWithMeta(1);
        } else if (message.includes("Le soldat a été déplacé") || message.includes("un ennemi en") || message.includes("capturée par le joueur")) {
            refreshPageWithMeta(1);
        }else if (message.includes("flashSuccess")) {
            let txt = message.replace("flashSuccess", "");
            refreshPageWithMeta(1);
            showNotification(txt, 'success');
        } else if (message.includes("flashErreur")) {
            let txt = message.replace("flashErreur", "");
            refreshPageWithMeta(1);
            showNotification(txt, 'error');
        }else {
            if (message === "CLEAR_CHAT") {
                // Effacer les données du chat
                chatBox.innerHTML = '';
                console.log("Chat vidé par le serveur.");
            } else {
                // Ajouter le message reçu au chat
                const messageElement = document.createElement('div');
                messageElement.textContent = message;
                chatBox.appendChild(messageElement);
                chatBox.scrollTop = chatBox.scrollHeight; // Scroll to the bottom
            }
        }
    };

    // Envoyer un message via le WebSocket
    function sendMessage() {
        const chatInput = document.getElementById('chatInput');
        const message = chatInput.value.trim();

        if (message) {
            socket.send(message);
            chatInput.value = '';
            saveChatMessage(`Vous: ${message}`); // Sauvegarder les messages envoyés
        }
    }

    // Charger les messages sauvegardés au chargement de la page
    document.addEventListener('DOMContentLoaded', loadChatMessages);
</script>

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

    function submitAction(direction) {
        const now = new Date().getTime();

        // Empêche les soumissions répétées rapides
        if (lastSubmitted && now - lastSubmitted < 500) {
            console.warn("Double soumission évitée.");
            return;
        }

        lastSubmitted = now;

        // Définir la direction choisie dans le champ caché
        document.getElementById('directionInput1').value = direction;
        // Soumettre le formulaire
        document.getElementById('actionsForm').setAttribute("action", "actions");
        document.getElementById('actionsForm').submit();
    }

    function refreshPageWithMeta(interval) {
        // Créer l'élément <meta> pour le rafraîchissement
        const metaRefresh = document.createElement('meta');
        metaRefresh.setAttribute('http-equiv', 'refresh');
        metaRefresh.setAttribute('content', interval); // Intervalle en secondes

        // Ajouter <meta> dans le <head>
        document.head.appendChild(metaRefresh);

        // Supprimer le <meta> après le rechargement de la page
        setTimeout(() => {
            document.head.removeChild(metaRefresh);
        }, interval * 1000); // Convertir l'intervalle en millisecondes
    }
</script>
</body>
</html>
