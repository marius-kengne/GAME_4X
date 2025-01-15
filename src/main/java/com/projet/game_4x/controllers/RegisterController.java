package com.projet.game_4x.controllers;

import com.projet.game_4x.models.Carte;
import com.projet.game_4x.models.Game;
import com.projet.game_4x.utils.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "RegisterController", value = "/register")
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("Views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        String result = inscrireJoueur(request, login, password);

        if ("success".equals(result)) {
            HttpSession session = request.getSession();
            session.setAttribute("success", "Compte créé avec success");
            response.sendRedirect("login");
        } else {
            request.setAttribute("erreur", result);
            request.getRequestDispatcher("Views/register.jsp").forward(request, response);
        }
    }

    private String inscrireJoueur(HttpServletRequest request, String login, String password) {
        try (Connection connection = DBConnection.getConnection()) {
            // Vérification si le login existe déjà
            String checkQuery = "SELECT id FROM joueurs WHERE login = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, login);
            ResultSet resultSet = checkStmt.executeQuery();
            if (resultSet.next()) {
                return "Le login est déjà utilisé.";
            }

            // Création du joueur
            String insertJoueurQuery = "INSERT INTO joueurs (login, mot_de_passe) VALUES (?, ?)";
            PreparedStatement insertJoueurStmt = connection.prepareStatement(insertJoueurQuery, Statement.RETURN_GENERATED_KEYS);
            insertJoueurStmt.setString(1, login);
            insertJoueurStmt.setString(2, password);
            insertJoueurStmt.executeUpdate();

            // Récupérer l'ID du joueur
            ResultSet generatedKeys = insertJoueurStmt.getGeneratedKeys();
            if (!generatedKeys.next()) {
                return "Erreur lors de la création du joueur.";
            }
            int joueurId = generatedKeys.getInt(1);

            // Récupération de l'ID de la carte
            //Object carteIdObj = getServletContext().getAttribute("carteId");
            Game game = Game.getInstance();
            Carte carteIdObj = game.getCarte();
            if (carteIdObj == null) {
                return "La carte n'a pas été initialisée.";
            }
            int carteId = (int) carteIdObj.getId();

            // Trouver une tuile vide sur la carte
            String findTuileQuery = "SELECT id FROM tuiles WHERE carte_id = ? AND type = 'vide' AND proprietaire_id IS NULL LIMIT 1";
            PreparedStatement findTuileStmt = connection.prepareStatement(findTuileQuery);
            findTuileStmt.setInt(1, carteId);
            ResultSet tuileResult = findTuileStmt.executeQuery();
            if (!tuileResult.next()) {
                return "Aucune tuile disponible pour le joueur.";
            }
            int tuileId = tuileResult.getInt("id");

            // Associer la tuile au joueur
            String updateTuileQuery = "UPDATE tuiles SET proprietaire_id = ? WHERE id = ?";
            PreparedStatement updateTuileStmt = connection.prepareStatement(updateTuileQuery);
            updateTuileStmt.setInt(1, joueurId);
            updateTuileStmt.setInt(2, tuileId);
            updateTuileStmt.executeUpdate();

            // Créer un soldat sur la tuile
            String insertSoldatQuery = "INSERT INTO soldats (proprietaire_id, position_tuile_id) VALUES (?, ?)";
            PreparedStatement insertSoldatStmt = connection.prepareStatement(insertSoldatQuery);
            insertSoldatStmt.setInt(1, joueurId);
            insertSoldatStmt.setInt(2, tuileId);
            insertSoldatStmt.executeUpdate();

            return "success";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Une erreur est survenue lors de l'inscription.";
        }
    }
}
