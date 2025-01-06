package com.projet.game_4x.controllers;


import com.projet.game_4x.utils.DBConnection;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import com.projet.game_4x.DAO.JoueurDAO;
import com.projet.game_4x.models.Joueur;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
            Joueur joueur = JoueurDAO.authenticateJoueur(connection, login, password);

            if (joueur == null) {
                request.setAttribute("erreur", "Login ou mot de passe incorrect.");
                request.getRequestDispatcher("Views/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("joueur", joueur);
            System.out.println("utilisatuer connecté");
            response.sendRedirect("game");
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("erreur", "Une erreur est survenue lors de la connexion.");
            request.getRequestDispatcher("Views/login.jsp").forward(request, response);
        }
    }
}