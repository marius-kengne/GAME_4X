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

@WebServlet(name = "InscriptionController", value = "/inscription")
public class InscriptionController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupération des paramètres du formulaire
        String login = request.getParameter("login");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("mot_de_passe");

        // Messages d'erreur pour la validation des champs
        StringBuilder errorMessage = new StringBuilder();

        if (login == null || login.trim().isEmpty()) {
            errorMessage.append("Le champ 'Login' est obligatoire.<br>");
        }
        if (email == null || email.trim().isEmpty()) {
            errorMessage.append("Le champ 'Email' est obligatoire.<br>");
        }
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            errorMessage.append("Le champ 'Mot de passe' est obligatoire.<br>");
        }

        // Si des champs sont manquants, afficher les erreurs
        if (errorMessage.length() > 0) {
            request.setAttribute("error", errorMessage.toString());
            RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Connexion à la base de données et vérification
        try (Connection conn = DatabaseUtils.getConnection()) {
            System.out.println("Connexion établie à la base de données...");

            // Vérification si le login ou l'email existe déjà
            String checkSql = "SELECT * FROM joueurs WHERE login = ? OR email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, login);
            checkStmt.setString(2, email);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                // Si le login ou l'email existe déjà
                request.setAttribute("error", "Login ou email déjà utilisé !");
                RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
                dispatcher.forward(request, response);
            } else {
                // Insertion dans la base de données
                String insertSql = "INSERT INTO joueurs (login, email, mot_de_passe) VALUES (?, ?, SHA2(?, 256))";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, login);
                insertStmt.setString(2, email);
                insertStmt.setString(3, motDePasse);
                insertStmt.executeUpdate();
                System.out.println("Inscription réussie pour l'utilisateur : " + login);

                // Redirection vers la page de connexion avec un message de succès
                response.sendRedirect(request.getContextPath() + "/login.jsp?success=1");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            String detailedError = "Erreur lors de la connexion à la base de données : " + e.getMessage();
            request.setAttribute("error", detailedError);
            RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur inattendue s'est produite : " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Redirection vers la page d'inscription si accès direct en GET
        RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
        dispatcher.forward(request, response);
    }
}