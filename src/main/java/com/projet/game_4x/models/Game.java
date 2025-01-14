package com.projet.game_4x.models;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private static Game instance; // Singleton instance
    private Carte carte;
    private List<Integer> joueurs; // Liste des IDs des joueurs connectés
    private int currentPlayer; // Joueur actuel
    private boolean start = false; //status du game start ou end

    private Game() {
        this.joueurs = new ArrayList<>();
        //this.currentPlayer = 1; // Le joueur 1 commence par défaut
    }

    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public Carte getCarte() {
        return carte;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
    }

    public List<Integer> getJoueurs() {
        return joueurs;
    }

    public void addJoueur(int joueurId) {
        if (!joueurs.contains(joueurId)) {
            joueurs.add(joueurId);
        }
    }

    public synchronized int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    public synchronized void nextPlayerOld() {
        int totalPlayers = joueurs.size();
        currentPlayer = (currentPlayer % totalPlayers) + 1;
    }

    public synchronized void nextPlayer() {
        if (joueurs.isEmpty()) {
            throw new IllegalStateException("La liste des joueurs est vide. Impossible de définir le prochain joueur.");
        }

        // Trouver l'index du joueur actuel dans la liste
        int currentIndex = joueurs.indexOf(currentPlayer);

        // Si le joueur actuel n'est pas trouvé (par exemple, première exécution), commencer par le premier joueur
        if (currentIndex == -1) {
            currentPlayer = joueurs.get(0); // Définir le premier joueur comme joueur actuel
        } else {
            // Calculer l'index du prochain joueur
            int nextIndex = (currentIndex + 1) % joueurs.size();
            currentPlayer = joueurs.get(nextIndex); // Obtenir l'ID du prochain joueur
        }
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}