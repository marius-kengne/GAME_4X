package com.projet.game_4x.controllers;

import com.projet.game_4x.models.Carte;
import com.projet.game_4x.models.Joueur;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
@WebServlet("/game")
public class FrontControllerServlet extends HttpServlet{

    /*
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException, ServletException {
        // Récupérer la session et les données nécessaires

        HttpSession session = request.getSession();
        Joueur joueur = (Joueur) session.getAttribute("joueur");

        if (joueur == null) {
            joueur = new Joueur(1, "JoueurFictif", "password123", 0, 50); // Exemple avec 50 points de production
            session.setAttribute("joueur", joueur);
            //response.sendRedirect("Views/login.jsp");
            //return;
        }

        // Charger la carte (par exemple depuis la base de données ou générer aléatoirement)
        Carte carte = new Carte(1, 10, 10);
        carte.genererCarteAleatoire();
        request.setAttribute("carte", carte);

        // Attribuer le tour actuel
        request.setAttribute("tourActuel", 1); // Exemple fixe

        // Rediriger vers la vue
        RequestDispatcher dispatcher = request.getRequestDispatcher("Views/game.jsp");
        dispatcher.forward(request, response);
    }

    */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer la session
        HttpSession session = request.getSession();

        // Récupérer l'objet Joueur depuis la session (ou en créer un pour les tests)
        Joueur joueur = (Joueur) session.getAttribute("joueur");
        if (joueur == null) {
            joueur = new Joueur(1, "JoueurFictif", "password123", 0, 50); // Exemple de joueur fictif
            session.setAttribute("joueur", joueur);
        }

        // Récupérer la carte depuis la session ou la générer si elle n'existe pas
        Carte carte = (Carte) session.getAttribute("carte");
        if (carte == null) {
            carte = new Carte(1, 8, 8); // Taille 10x10
            carte.genererCarteAleatoire(); // Générer la carte une seule fois
            session.setAttribute("carte", carte); // Stocker la carte dans la session
        }

        // Attribuer des informations supplémentaires pour l'affichage
        request.setAttribute("tourActuel", 1); // Exemple : fixer le tour actuel à 1
        request.setAttribute("carte", carte); // Passer la carte à la vue

        // Rediriger vers la page de jeu
        request.getRequestDispatcher("/Views/game.jsp").forward(request, response);
    }

}
