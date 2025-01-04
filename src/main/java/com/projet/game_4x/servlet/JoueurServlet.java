package com.projet.game_4x.servlet;
import com.projet.game_4x.utils.DatabaseUtils;

import jakarta.servlet.RequestDispatcher;
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

// Annotation pour mapper le Servlet à une URL
@WebServlet(name = "joueurServlet", value = "/joueur-action")
public class JoueurServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("inscription".equals(action)) {
            handleInscription(request, response);
        } else if ("logout".equals(action)) {
            handleLogout(request, response);
        } else {
            response.sendRedirect("index.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    // Méthode pour gérer l'inscription d'un joueur
    private void handleInscription(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("mot_de_passe");

        try (Connection conn = DatabaseUtils.getConnection()) {
            // Vérification si le pseudo ou l'email existe déjà
            String checkSql = "SELECT * FROM joueurs WHERE login = ? OR email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, login);
            checkStmt.setString(2, email);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                // Le pseudo ou l'email existe déjà
                request.setAttribute("error", "Pseudo ou email déjà utilisé !");
                RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
                dispatcher.forward(request, response);
            } else {
                // Insertion du joueur dans la base de données
                String sql = "INSERT INTO joueurs (login, email, mot_de_passe) VALUES (?, ?, SHA2(?, 256))";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, login);
                stmt.setString(2, email);
                stmt.setString(3, motDePasse);
                stmt.executeUpdate();
                response.sendRedirect("login.jsp?success=1"); // Redirection après succès
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur serveur lors de l'inscription.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("Views/inscription.jsp");
            dispatcher.forward(request, response);
        }
    }

    // Méthode pour gérer la connexion d'un joueur



    // Méthode pour gérer la déconnexion d'un joueur
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.invalidate(); // Invalider la session
        response.sendRedirect("login.jsp?logout=1"); // Redirection après déconnexion
    }
}