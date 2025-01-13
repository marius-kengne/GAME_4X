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
        this.currentPlayer = 1; // Le joueur 1 commence par défaut
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

    public synchronized void nextPlayer() {
        int totalPlayers = joueurs.size();
        currentPlayer = (currentPlayer % totalPlayers) + 1;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
}