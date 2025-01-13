package com.projet.game_4x.controllers;

import com.projet.game_4x.utils.DBConnection;
import com.projet.game_4x.models.Joueur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/score")
public class ScoreController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);  // Récupérer la session en cours sans en créer une nouvelle

        if (session == null || session.getAttribute("joueur") == null) {
            // Si la session n'est pas active ou l'utilisateur non connecté
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp");
            return;
        }

        Joueur joueur = (Joueur) session.getAttribute("joueur");

        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT score FROM joueurs WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, joueur.getId());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int score = rs.getInt("score");
                request.setAttribute("score", score);
                request.getRequestDispatcher("/Views/score.jsp").forward(request, response);
            } else {
                throw new Exception("Le joueur avec l'ID " + joueur.getId() + " est introuvable dans la base de données.");
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Affiche l'erreur SQL complète dans la console
            request.setAttribute("errorMessage", "Erreur SQL : " + e.getMessage());
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();  // Affiche l'erreur générique
            request.setAttribute("errorMessage", "Erreur lors de la récupération du score : " + e.getMessage());
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Méthode pour ajouter des points au score via le formulaire
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("joueur") == null) {
            response.sendRedirect(request.getContextPath() + "/Views/login.jsp");
            return;
        }

        Joueur joueur = (Joueur) session.getAttribute("joueur");

        // Validation de l'entrée
        String pointsStr = request.getParameter("points");
        int pointsToAdd;
        try {
            pointsToAdd = Integer.parseInt(pointsStr);  // Points à ajouter envoyés par le formulaire
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Veuillez entrer un nombre valide.");
            request.getRequestDispatcher("/Views/score.jsp").forward(request, response);
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            // Mise à jour du score dans la base de données
            String updateQuery = "UPDATE joueurs SET score = score + ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(updateQuery);
            stmt.setInt(1, pointsToAdd);
            stmt.setInt(2, joueur.getId());
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                // Récupérer le score mis à jour
                String selectQuery = "SELECT score FROM joueurs WHERE id = ?";
                PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                selectStmt.setInt(1, joueur.getId());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int updatedScore = rs.getInt("score");
                    joueur.setScore(updatedScore);  // Mettre à jour l'objet joueur avec le nouveau score

                    // Mettre à jour la session avec le nouvel objet joueur
                    session.setAttribute("joueur", joueur);
                }

                // Redirection pour voir le score à jour
                response.sendRedirect(request.getContextPath() + "/score");
            } else {
                throw new Exception("Impossible de mettre à jour le score pour le joueur ID " + joueur.getId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur SQL lors de la mise à jour du score : " + e.getMessage());
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur lors de la mise à jour du score : " + e.getMessage());
            request.getRequestDispatcher("/Views/error.jsp").forward(request, response);
        }
    }
}