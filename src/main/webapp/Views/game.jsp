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
        table {
            border-collapse: collapse;
            width: auto; /* La table s'adapte au contenu */
        }
        td {
            width: 50px; /* Largeur des cellules */
            height: 50px; /* Hauteur des cellules pour que les cellules soient carrées */
            text-align: center;
            border: 1px solid black;
        }
        img {
            width: 80px; /* Taille des images pour s'adapter aux cellules */
            height: 80px;
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

        /* Styles pour la pop-up */
        .popup {
            min-width: 450px;
            min-height: 150px;
            display: none; /* Cacher la pop-up par défaut */
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
            display: none; /* Cacher l'overlay par défaut */
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

        #actions{
            margin-top: 10px;
        }
    </style>
    <script>
        function showPopup() {
            // Afficher la pop-up et l'overlay
            document.getElementById("popup").style.display = "block";
            document.getElementById("popup-overlay").style.display = "block";
        }

        function closePopup() {
            // Masquer la pop-up et l'overlay
            document.getElementById("popup").style.display = "none";
            document.getElementById("popup-overlay").style.display = "none";
        }
    </script>
</head>
<body>

<center>
    <h1>Bienvenue, ${joueur.login} !</h1>
    <button onclick="showPopup()" style="margin-bottom: 10px">Voir les informations du tour</button>
</center>

<!-- Pop-up -->
<div id="popup-overlay" class="popup-overlay" onclick="closePopup()"></div>
<div id="popup" class="popup">
    <h3>Tour actuel : ${tourActuel}</h3>
    <h3>Points de production : ${joueur.pointsProduction}</h3>
    <button class="close-btn" onclick="closePopup()">Fermer</button>
</div>

<!-- Grille de la carte -->
<center>
    <div id="game-board">
        <table style="background: #cccccc">
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
                                        <img src="resources/icons/Large/soldier.png" alt="Soldat">
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>
    </div>
</center>

<!-- Actions disponibles -->
<div id="actions">
    <!--form action="actions" method="post" style="display: flex; gap: 10px; justify-content: center; align-items: center;">
        <button type="submit" name="action" value="move">Déplacer un soldat</button>
        <button type="submit" name="action" value="attack">Attaquer</button>
        <button type="submit" name="action" value="forage">Collecter des ressources</button>
        <button type="submit" name="action" value="heal">Soigner un soldat</button>
        <button type="submit" name="action" value="recruit">Recruter un soldat</button>
        <button type="submit" name="action" value="endTurn">Passer le tour</button>
    </form-->
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

<!-- Rafraîchissement automatique -->
<!--meta http-equiv="refresh" content="5"-->
</body>
</html>
