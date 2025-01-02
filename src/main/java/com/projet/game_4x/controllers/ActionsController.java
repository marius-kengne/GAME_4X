package com.projet.game_4x.controllers;

import com.projet.game_4x.models.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "ActionsController", value = "/actions")
public class ActionsController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupérer la carte et les joueurs depuis le contexte
        Carte carte = (Carte) getServletContext().getAttribute("carte");
        List<Joueur> joueurs = (List<Joueur>) getServletContext().getAttribute("joueurs");
        int tourActuel = (int) getServletContext().getAttribute("tourActuel");

        Joueur joueurActuel = joueurs.get(tourActuel);

        // Récupérer l'action depuis le formulaire
        String action = request.getParameter("action");
        String message = null;

        // Gérer les actions
        switch (action) {
            case "moveNorth":
                message = deplacerSoldat(request, joueurActuel, 0, -1, carte);
                break;
            case "moveSouth":
                message = deplacerSoldat(request, joueurActuel, 0, 1, carte);
                break;
            case "moveEast":
                message = deplacerSoldat(request, joueurActuel, 1, 0, carte);
                break;
            case "moveWest":
                message = deplacerSoldat(request, joueurActuel, -1, 0, carte);
                break;
            case "heal":
                message = soignerSoldat(request, joueurActuel);
                break;
            case "forage":
                message = forager(request, joueurActuel);
                break;
            case "endTurn":
                // Passer au joueur suivant
                tourActuel = (tourActuel + 1) % joueurs.size();
                getServletContext().setAttribute("tourActuel", tourActuel);
                message = "Tour passé au joueur suivant.";
                break;
            default:
                message = "Action non reconnue !";
        }

        // Stocker les messages et mettre à jour la vue
        request.setAttribute("message", message);
        request.setAttribute("carte", carte);
        request.setAttribute("joueur", joueurs.get(tourActuel));
        request.setAttribute("tourActuel", tourActuel);
        request.getRequestDispatcher("game.jsp").forward(request, response);
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
        /*
        Tuile tuile = joueur.getCarte().getTuile(x, y);
        if (tuile != null && tuile.getSoldat() != null) {
            Soldat soldat = tuile.getSoldat();
            soldat.setPointsDeVie(Math.min(10, soldat.getPointsDeVie() + 5)); // Soigne 5 points
            return "Le soldat a été soigné.";
        }*/
        return "Aucun soldat à soigner.";
    }

    private String forager(HttpServletRequest request, Joueur joueur) {
        int x = Integer.parseInt(request.getParameter("x"));
        int y = Integer.parseInt(request.getParameter("y"));

        /*Tuile tuile = joueur.getCarte().getTuile(x, y);
        if (tuile != null && tuile.getType().equals("foret")) {
            joueur.setPointsProduction(joueur.getPointsProduction() + 5);
            return "Points de production collectés.";
        }*/
        return "Aucune ressource à collecter.";
    }
}
