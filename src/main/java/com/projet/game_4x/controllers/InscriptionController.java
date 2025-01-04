package com.projet.game_4x.controllers;

import com.projet.game_4x.utils.DatabaseUtils;
import jakarta.servlet.annotation.WebServlet;
import com.projet.game_4x.models.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@WebServlet(name ="InscriptionController", value =  "/start_game")
public class InscriptionController extends HttpServlet {

    // Affichage du formulaire d'inscription (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
        dispatcher.forward(request, response);
    }

    // Traitement de l'inscription (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pseudo = request.getParameter("pseudo");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("mot_de_passe");

        try (Connection conn = DatabaseUtils.getConnection()) {
            // Vérification si le pseudo ou l'email existe déjà
            String checkSql = "SELECT * FROM joueurs WHERE pseudo = ? OR email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, pseudo);
            checkStmt.setString(2, email);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                // Le pseudo ou l'email existe déjà
                request.setAttribute("error", "Pseudo ou email déjà utilisé !");
                RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
                dispatcher.forward(request, response);
            } else {
                // Insertion du joueur dans la base de données
                String sql = "INSERT INTO joueurs (pseudo, email, mot_de_passe) VALUES (?, ?, SHA2(?, 256))";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, pseudo);
                stmt.setString(2, email);
                stmt.setString(3, motDePasse);
                stmt.executeUpdate();

                // Redirection vers la page de connexion après succès
                response.sendRedirect("login.jsp?success=1");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur serveur lors de l'inscription.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
            dispatcher.forward(request, response);
        }
    }
}