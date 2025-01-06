package com.projet.game_4x.controllers;

import com.projet.game_4x.models.*;
import com.projet.game_4x.utils.DBConnection;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            Joueur joueur = authentifierJoueur(connection, login, password);

            if (joueur == null) {
                request.setAttribute("erreur", "Login ou mot de passe incorrect.");
                request.getRequestDispatcher("Views/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("joueur", joueur);

            response.sendRedirect("game");
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