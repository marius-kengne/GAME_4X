package com.projet.game_4x.controllers;

import com.projet.game_4x.models.Carte;
import com.projet.game_4x.models.Joueur;
import com.projet.game_4x.models.Soldat;
import com.projet.game_4x.models.Tuile;
import com.projet.game_4x.utils.DBConnection;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet(name = "FrontControllerServlet", value = "/game")
public class FrontControllerServlet extends HttpServlet {

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

            // Vérification si l'action est un recrutement pour recharger les soldats
            String action = request.getParameter("action");
            if ("recruterSoldat".equals(action)) {
                chargerSoldats(connection, carte);  // Rafraîchir la carte pour inclure le nouveau soldat
            }

            int tourActuel = (int) getServletContext().getAttribute("tourActuel");

            request.setAttribute("carte", carte);
            request.setAttribute("tourActuel", tourActuel);
            request.setAttribute("joueur", joueur);

            request.getRequestDispatcher("Views/game.jsp").forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("Erreur lors du chargement de la carte", e);
        }
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