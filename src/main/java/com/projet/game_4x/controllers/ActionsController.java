package com.projet.game_4x.controllers;

import com.projet.game_4x.models.*;
import com.projet.game_4x.utils.GameWebSocket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "ActionsController", value = "/actions")
public class ActionsController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Récupérer l'action depuis le formulaire
        String action = request.getParameter("direction");
        String message;

        HttpSession session = request.getSession();
        Object tourActuel = session.getAttribute("tourActuel");


        // Gérer les actions
        switch (action) {
            case "endTurn":
                // Passer au joueur suivant
                Game game = Game.getInstance();
                game.nextPlayer();
                if (tourActuel == null){
                    session.setAttribute("tourActuel", 0);
                }else {
                    int tour = (int) session.getAttribute("tourActuel");
                    session.setAttribute("tourActuel", tour+1);
                }
                GameWebSocket.broadcast("Le soldat a été déplacé vers la position X=");
                break;
            default:
                message = "Action non reconnue !";
        }

        response.sendRedirect("game");
    }

    private String deplacerSoldat(HttpServletRequest request, Joueur joueur, int dx, int dy, Carte carte) {
        int x = Integer.parseInt(request.getParameter("x"));
        int y = Integer.parseInt(request.getParameter("y"));

        Tuile origine = carte.getTuile(x, y);
        Tuile destination = carte.getTuile(x + dx, y + dy);

        if (origine == null || origine.getSoldat() == null || !origine.getSoldat().getProprietaire().equals(joueur)) {
            return "Aucun soldat à déplacer ou déplacement non autorisé.";
        }

        if (destination == null || destination.getType().equals("montagne")) {
            return "Déplacement impossible (montagne ou hors carte).";
        }

        // Déplacer le soldat
        Soldat soldat = origine.getSoldat();
        origine.setSoldat(null);
        destination.setSoldat(soldat);
        soldat.setPosition(destination);

        return "Soldat déplacé.";
    }

    private String soignerSoldat(HttpServletRequest request, Joueur joueur) {
        int x = Integer.parseInt(request.getParameter("x"));
        int y = Integer.parseInt(request.getParameter("y"));
        Carte carte = (Carte) getServletContext().getAttribute("carte");

        Tuile tuile = carte.getTuile(x, y);
        if (tuile != null && tuile.getSoldat() != null && tuile.getSoldat().getProprietaire().equals(joueur)) {
            Soldat soldat = tuile.getSoldat();
            soldat.soigner(5); // Soigne 5 points
            return "Le soldat a été soigné.";
        }
        return "Aucun soldat à soigner.";
    }

    private String forager(HttpServletRequest request, Joueur joueur) {
        int x = Integer.parseInt(request.getParameter("x"));
        int y = Integer.parseInt(request.getParameter("y"));
        Carte carte = (Carte) getServletContext().getAttribute("carte");

        Tuile tuile = carte.getTuile(x, y);
        if (tuile != null && tuile.getType().equals("foret") && tuile.getSoldat() != null && tuile.getSoldat().getProprietaire().equals(joueur)) {
            joueur.setPointsProduction(joueur.getPointsProduction() + 5);
            return "Points de production collectés.";
        }
        return "Aucune ressource à collecter.";
    }
}
