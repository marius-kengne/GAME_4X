package com.projet.game_4x.controllers;

import com.projet.game_4x.models.*;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "GameInitController", value = "/start-game")
public class GameInitController extends HttpServlet {

    private Carte carte;
    private List<Joueur> joueurs;

    @Override
    public void init() throws ServletException {
        // Générer une carte aléatoire
        this.carte = new Carte(1, 10, 10);
        carte.genererCarteAleatoire();

        // Initialisation des joueurs
        this.joueurs = new ArrayList<>();
        Joueur joueur1 = new Joueur(1, "Joueur1", "password1", 0, 10);
        Joueur joueur2 = new Joueur(2, "Joueur2", "password2", 0, 10);

        joueurs.add(joueur1);
        joueurs.add(joueur2);

        // Placement des soldats sur des villes aléatoires
        placerSoldatSurVille(joueur1);
        placerSoldatSurVille(joueur2);

        // Stocker les données dans le contexte de l'application
        getServletContext().setAttribute("carte", carte);
        getServletContext().setAttribute("joueurs", joueurs);
        getServletContext().setAttribute("tourActuel", 0); // Le premier joueur commence
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Charger la carte et le joueur actuel
        int tourActuel = (int) getServletContext().getAttribute("tourActuel");
        Joueur joueurActuel = joueurs.get(tourActuel);

        // Passer les données à la vue
        request.setAttribute("carte", carte);
        request.setAttribute("joueur", joueurActuel);
        request.setAttribute("tourActuel", tourActuel);
        request.getRequestDispatcher("/Views/game.jsp").forward(request, response);
    }

    // Place un soldat d'un joueur sur une ville aléatoire
    private void placerSoldatSurVille(Joueur joueur) {
        for (Tuile tuile : carte.getTuiles()) {
            if (tuile.getType().equals("ville") && tuile.getProprietaire() == null) {
                tuile.setProprietaire(joueur); // Associer la ville au joueur
                Soldat soldat = new Soldat(1, joueur, tuile, 10, 5, 3); // Créer un soldat avec stats par défaut
                tuile.setSoldat(soldat); // Associer le soldat à la tuile
                break;
            }
        }
    }
}
