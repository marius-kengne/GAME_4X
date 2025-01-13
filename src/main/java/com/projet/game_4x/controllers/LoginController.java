package com.projet.game_4x.controllers;


import com.projet.game_4x.DAO.JoueurDAO;
import com.projet.game_4x.models.Joueur;
import com.projet.game_4x.utils.DBConnection;
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

        try (Connection connection = DBConnection.getConnection()) {
            Joueur joueur = JoueurDAO.authenticateJoueur(connection, login, password);

            if (joueur == null) {
                request.setAttribute("erreur", "Login ou mot de passe incorrect.");
                request.getRequestDispatcher("Views/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("joueur", joueur);
            session.setAttribute("tourActuel", 0);
            System.out.println("utilisatuer connecté");
            //response.sendRedirect("game");
            response.sendRedirect("home");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Une erreur est survenue lors de la connexion.");
            request.getRequestDispatcher("Views/login.jsp").forward(request, response);
        }
    }


    private Joueur authentifierJoueur(Connection connection, String login, String password) throws SQLException {
        String query = "SELECT id, score, points_de_production FROM joueurs WHERE login = ? AND mot_de_passe = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, login);
        stmt.setString(2, password);

        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            return new Joueur(
                    resultSet.getInt("id"),
                    login,
                    password,
                    resultSet.getInt("score"),
                    resultSet.getInt("points_de_production")
            );
        }
        return null;
    }
}