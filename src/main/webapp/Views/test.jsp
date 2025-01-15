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
  <!--meta http-equiv="refresh" content="2"-->
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

    function checkTurn() {
      let currentPlayer = <%=game.getCurrentPlayer()%>;
      let playerId = <%=joueur.getId()%>;
      if (currentPlayer == playerId){
        document.getElementById('turn-info').innerText = "C'est votre tour !";
        document.getElementById('actions').style.display = "block";
      }else {
        console.log("### status not set #######");
        document.getElementById('turn-info').innerText = "En attente...";
        document.getElementById('actions').style.display = "none";
      }
    }
    setInterval(checkTurn, 2000);
  </script>
  <!--script>
        function checkTurn() {
            fetch('/game_4x/turnStatus')
                .then(response => response.json())
                .then(data => {
                    console.log("### start set status #######");
                    const currentPlayer = data.currentPlayer;
                    if (currentPlayer === <%= playerId %>) {
                        console.log("### status set #######");
                        //location.reload();
                        document.getElementById('turn-info').innerText = "C'est votre tour !";
                        document.getElementById('actions').style.display = "block";
                    } else {
                        console.log("### status not set #######");
                        document.getElementById('turn-info').innerText = "En attente...";
                        document.getElementById('actions').style.display = "none";
                    }
                });
        }
        setInterval(checkTurn, 2000); // Vérification toutes les 2 secondes
    </script-->
</head>
<body>

<!--h1>game= ${game.getCarte().toString()}</h1-->
<h1>Bienvenue, ${joueur.login} !</h1>
<button onclick="showPopup()">Voir les informations du tour</button>
<br>
<form id="score" action="score" method="get" style="display: flex; gap: 10px; justify-content: center; align-items: center;">
  <button type="submit">Voir Score</button>
</form>
<!-- Pop-up -->
<div id="popup-overlay" class="popup-overlay" onclick="closePopup()"></div>
<div id="popup" class="popup">
  <h3>Tour actuel : ${tourActuel}</h3>
  <h3>Points de production : ${joueur.pointsProduction}</h3>
  <button class="close-btn" onclick="closePopup()">Fermer</button>
</div>

<!-- Affichage des messages -->
<%
  String erreur = (String) session.getAttribute("erreur");
  String message = (String) session.getAttribute("message");

  // Supprimez les messages après les avoir affichés
  session.removeAttribute("erreur");
  session.removeAttribute("message");
%>

<%
  String error = (String) session.getAttribute("flashErreur");
  session.removeAttribute("flashErreur"); // Supprimer après utilisation
  String success = (String) session.getAttribute("flashSuccess");
  session.removeAttribute("flashSuccess"); // Supprimer après utilisation
%>

<% if (error != null) { %>
<div class="error-message" style="color: red;">
  <br>
  <%= error %>
</div>
<% } %>
<% if (success != null) { %>
<div class="success-message" style="color: green;">
  <br>
  <%= success %>
</div>
<% } %>
<!-- Message de Notification du tour du Joueur de Jouer -->
<h2 id="turn-info">Chargement...</h2>

<% if (game.getCurrentPlayer() == joueur.getId()) { %>
  <h2 id="turn-info">C'est ton tour de jour</h2>
<% } else { %>
<p>En attente du tour des autres joueurs...</p>
<% } %>


<% if (erreur != null) { %>
<div class="error-message" style="color: red;"><%= erreur %></div>
<% } %>

<% if (message != null) { %>
<div class="success-message" style="color: green;">
  <% if (message.contains("Ville capturée")) { %>
  <strong>Succès :</strong> <%= message %>
  <% } else { %>
  <%= message %>
  <% } %>
</div>
<% } %>
<!-- Grille de la carte -->
<div id="game-board">
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
</div>

<!-- Actions disponibles -->

<div id="actions">
  <% if (game.getCurrentPlayer() == joueur.getId()) { %>
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
    <button type="button" style="background: gainsboro" onclick="submitAction('recruit')">Recruit a soldier</button>
    <button type="submit" name="action" value="heal">Heal</button>
    <button type="submit" name="action" value="forage">Forage</button>
    <button type="submit" onclick="submitAction('endTurn')">End Turn</button>
  </form>
  <% } else { %>
  <p>En attente du tour des autres joueurs...</p>
  <% } %>
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

  function submitAction(direction) {
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
    document.getElementById('deplacementForm').setAttribute("action", "actions");
    document.getElementById('deplacementForm').submit();
  }
</script>

<script>

  // Connecter au WebSocket
  //const playerId = ${joueur.login}; // Id unique du joueur
  const socket = new WebSocket(`ws://192.168.1.167:8082/game_4x/gameUpdates/${joueur.login}`);
  //const socket = new WebSocket(`ws://172.20.10.13:8082/game_4x/gameUpdates/${joueur.login}`);

  // Quand une connexion est ouverte
  socket.onopen = function () {
    console.log("Connexion WebSocket ouverte.");
  };

  // Quand un message est reçu
  socket.onmessage = function (event) {
    const message = event.data;
    if (message.includes("Ville capturée")) {
      refreshPageWithMeta(1); // Rafraîchit la page après une seconde
      alert(message); // Affiche une alerte avec le message de capture
    } else if (message.includes("Le soldat a été déplacé") || message.includes("un ennemi en") || message.includes("capturée par le joueur")) {
      refreshPageWithMeta(1);
    }
    console.log("Message reçu : " + message);
  };

  // Quand une connexion est fermée
  socket.onclose = function () {
    console.log("Connexion WebSocket fermée.");
  };

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