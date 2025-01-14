package com.projet.game_4x.controllers;

import com.projet.game_4x.models.Carte;
import com.projet.game_4x.models.Game;
import com.projet.game_4x.models.Joueur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "StartGameController", value = "/new_game")
public class StartGameController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer l'instance globale de jeu
        Game game = Game.getInstance();

        // Charger ou générer une carte si nécessaire
        synchronized (game) {
            if (game.getCarte() == null) {
                if (!game.isStart()) {
                    Carte carte = Carte.chargerOuGenererCarte(10, 10);
                    game.setCarte(carte);
                    getServletContext().setAttribute("carteId", carte.getId());
                }else {
                    game = (Game) getServletContext().getAttribute("game");
                }
            }
        }

        HttpSession session = request.getSession();
        Joueur joueur = (Joueur) session.getAttribute("joueur");
        //Integer joueurId = (Integer) session.getAttribute("playerId");
        Integer joueurId = joueur.getId();

        // Ajouter le joueur à la partie
        synchronized (game) {
            if (joueurId == null || !game.getJoueurs().contains(joueurId)) {
                //joueurId = game.getJoueurs().size() + 1;
                session.setAttribute("playerId", joueurId);
                game.addJoueur(joueurId);
            }

            // Initialiser le jeu si ce n'est pas encore fait
            if (!game.isStart()) {
                game.setStart(true);
                game.setCurrentPlayer(joueurId);
            }
        }

        // Stocker le jeu dans le contexte
        session.setAttribute("game", game);
        getServletContext().setAttribute("game", game);
        //getServletContext().setAttribute("tourActuel", game.getCurrentPlayer());


        // Rediriger vers la vue
        //request.getRequestDispatcher("Views/start.jsp").forward(request, response);
        response.sendRedirect("game");
    }
}