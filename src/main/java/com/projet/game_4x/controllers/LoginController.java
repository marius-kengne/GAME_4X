package com.projet.game_4x.controllers;

import com.projet.game_4x.utils.DBConnection;
import com.projet.game_4x.DAO.JoueurDAO;
import com.projet.game_4x.models.Joueur;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet(name = "LoginController", value = "/login")
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("Views/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        // Utilisation de la connexion depuis DBConnection
        try (Connection connection = DBConnection.getConnection()) {
            // Appel à la méthode authenticateJoueur avec une connexion
            Joueur joueur = JoueurDAO.authenticateJoueur(connection, login, password);

            if (joueur == null) {
                // Si les informations sont incorrectes
                request.setAttribute("erreur", "Login ou mot de passe incorrect.");
                request.getRequestDispatcher("Views/login.jsp").forward(request, response);
                return;
            }

            // Créer la session et stocker les informations du joueur connecté
            HttpSession session = request.getSession();
            session.setAttribute("joueur", joueur);
            System.out.println("Utilisateur connecté : " + joueur.getLogin());
            response.sendRedirect("game"); // Redirection vers la page du jeu après connexion réussie

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Une erreur est survenue lors de la connexion.");
            request.getRequestDispatcher("Views/login.jsp").forward(request, response);
        }
    }
}