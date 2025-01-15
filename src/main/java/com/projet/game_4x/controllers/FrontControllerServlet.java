package com.projet.game_4x.controllers;

import com.projet.game_4x.models.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "FrontControllerServlet", value = "/game")
public class FrontControllerServlet extends HttpServlet {

    /*
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Joueur joueur = (Joueur) session.getAttribute("joueur");

        if (joueur == null) {
            response.sendRedirect("login");
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            Carte carte = (Carte) getServletContext().getAttribute("carte");

            if (carte == null) {
                throw new ServletException("La carte n'a pas été initialisée.");
            }

            chargerSoldats(connection, carte);

            int tourActuel = (int) getServletContext().getAttribute("tourActuel");

            request.setAttribute("carte", carte);
            request.setAttribute("tourActuel", tourActuel);
            request.setAttribute("joueur", joueur);

            request.getRequestDispatcher("Views/game.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Erreur lors du chargement de la carte", e);
        }
    }
    */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Récupérer l'instance globale de jeu
        Game game = Game.getInstance();

        HttpSession session = request.getSession();
        Joueur joueur = (Joueur) session.getAttribute("joueur");

        if (joueur == null) {
            response.sendRedirect("login");
            return;
        }

        // Charger ou générer une carte si nécessaire
        synchronized (game) {
            if (game.getCarte() == null) {
                Carte carte = Carte.chargerOuGenererCarte(10, 10);
                game.setCarte(carte);
                getServletContext().setAttribute("carteId", carte.getId());
            }
        }

        //HttpSession session = request.getSession();
        //Joueur joueur = (Joueur) session.getAttribute("joueur");
        //Integer joueurId = (Integer) session.getAttribute("playerId");
        Integer joueurId = joueur.getId();

        // Ajouter le joueur à la partie
        synchronized (game) {
            if (joueurId == null || !game.getJoueurs().contains(joueurId)) {
                //joueurId = game.getJoueurs().size() + 1;
                session.setAttribute("playerId", joueurId);
                game.addJoueur(joueurId);
            }

            // Initialiser le jeu si ce n'est pas encore fait
            if (!game.isStart()) {
                game.setStart(true);
                game.setCurrentPlayer(joueurId);
            }
        }

        // Stocker le jeu dans le contexte
        getServletContext().setAttribute("game", game);
        //getServletContext().setAttribute("tourActuel", game.getCurrentPlayer());
        Object tourActuel = session.getAttribute("tourActuel");
        if (tourActuel == null){
            session.setAttribute("tourActuel", 0);
        }


        request.getRequestDispatcher("Views/start.jsp").forward(request, response);

    }
    private void chargerSoldats(Connection connection, Carte carte) throws SQLException {
        String query = """
                SELECT s.id, t.x, t.y, s.proprietaire_id, j.login
                FROM soldats s
                JOIN tuiles t ON s.position_tuile_id = t.id
                JOIN joueurs j ON s.proprietaire_id = j.id
            """;

        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet resultSet = stmt.executeQuery();

        while (resultSet.next()) {
            int x = resultSet.getInt("x");
            int y = resultSet.getInt("y");
            Tuile tuile = carte.getTuile(x, y);

            if (tuile != null) {
                Joueur proprietaire = new Joueur(
                        resultSet.getInt("proprietaire_id"),
                        resultSet.getString("login"),
                        "",
                        0,
                        0
                );

                Soldat soldat = new Soldat(
                        resultSet.getInt("id"),
                        proprietaire,
                        tuile,
                        100,
                        10,
                        5
                );

                tuile.setSoldat(soldat);
            }
        }
    }
}
