package com.projet.game_4x.controllers;

import com.projet.game_4x.DAO.JoueurDAO;
import com.projet.game_4x.models.Joueur;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

public class LoginController extends HttpServlet {

        @Override
        public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws ServletException, IOException {

            RequestDispatcher dispatcher = request.getRequestDispatcher("Views/login.jsp");
            dispatcher.forward(request, response);
        }

        @Override
        public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

            String login = request.getParameter("login");
            String mot_de_passe = request.getParameter("password");

            Joueur joueur = JoueurDAO.authenticateJoueur(login, mot_de_passe);


            if(joueur != null) {
                HttpSession session = request.getSession();
                session.setAttribute("Joueur", joueur);
                System.out.println("utilisatuer connecté");
            }
            else {
                request.setAttribute("error", "login ou mot de passe incorrect !");
                request.getRequestDispatcher("Views/login.jsp").forward(request, response);
                System.out.println("mot de passe ou login incorrect");
            }

        }


}
