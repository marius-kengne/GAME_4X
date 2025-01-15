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

        Game game = Game.getInstance();

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

        Integer joueurId = joueur.getId();


        synchronized (game) {
            if (joueurId == null || !game.getJoueurs().contains(joueurId)) {

                session.setAttribute("playerId", joueurId);
                game.addJoueur(joueurId);
            }

            if (!game.isStart()) {
                game.setStart(true);
                game.setCurrentPlayer(joueurId);
            }
        }

        session.setAttribute("game", game);
        getServletContext().setAttribute("game", game);

        response.sendRedirect("game");
    }
}