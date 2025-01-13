package com.projet.game_4x.controllers;

import com.projet.game_4x.models.*;
import com.projet.game_4x.DAO.JoueurDAO;
import com.projet.game_4x.utils.DBConnection;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "ActionsController", value = "/actions")
public class ActionsController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Récupération de la carte et des joueurs depuis le contexte
        Carte carte = (Carte) getServletContext().getAttribute("carte");
        List<Joueur> joueurs = (List<Joueur>) getServletContext().getAttribute("joueurs");
        int tourActuel = (int) getServletContext().getAttribute("tourActuel");

        Joueur joueurActuel = joueurs.get(tourActuel);

        // Récupération de l'action depuis le formulaire
        String action = request.getParameter("action");
        String message;

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
            case "recruterSoldat":
                message = recruterSoldat(joueurActuel, carte);
                break;
            case "endTurn":
                tourActuel = (tourActuel + 1) % joueurs.size();
                getServletContext().setAttribute("tourActuel", tourActuel);
                message = "Tour passé au joueur suivant.";
                break;
            default:
                message = "Action non reconnue !";
        }

        // Met à jour la vue avec le message
        request.setAttribute("message", message);
        request.setAttribute("carte", carte);
        request.setAttribute("joueur", joueurs.get(tourActuel));
        request.setAttribute("tourActuel", tourActuel);
        request.getRequestDispatcher("/Views/game.jsp").forward(request, response);
    }

    private String recruterSoldat(Joueur joueur, Carte carte) {
        final int COUT_RECRUTEMENT_SOLDAT = 15; // Coût en points de production

        if (!joueur.peutRecruterSoldat(COUT_RECRUTEMENT_SOLDAT)) {
            return "Vous n'avez pas assez de points de production pour recruter un soldat.";
        }

        try (Connection connection = DBConnection.getConnection()) {
            // Recherche d'une tuile vide aléatoire via JoueurDAO
            int tuileId = JoueurDAO.getRandomEmptyTuile(connection, carte.getId());
            if (tuileId == -1) {
                return "Aucune tuile vide disponible pour recruter un soldat.";
            }

            // Recrutement du soldat et mise à jour des points
            boolean success = JoueurDAO.recruterSoldat(connection, joueur.getId(), tuileId);
            if (!success) {
                return "Erreur lors de la création du soldat.";
            }
            // Deduire les points de productions dans l'objets joueur
            int nouveauxPointsProductions = joueur.getPointsProduction() - COUT_RECRUTEMENT_SOLDAT;
            joueur.setPointsProduction(nouveauxPointsProductions);
            JoueurDAO.updatePointsProduction(connection, joueur.getId(), joueur.getPointsProduction());

            return "Soldat recruté avec succès.";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Erreur SQL lors du recrutement du soldat : " + e.getMessage();
        }
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
            tuile.getSoldat().soigner(5); // Soigne 5 points
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