package com.projet.game_4x.models;

import java.util.ArrayList;
import java.util.List;

public class Joueur {
    private int id;
    private String login;
    private String motDePasse;
    private int score;
    private int pointsProduction;
    private List<Soldat> soldats; // Liste des soldats appartenant au joueur

    public Joueur(int id, String login, String motDePasse, int score, int pointsProduction) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.score = score;
        this.pointsProduction = pointsProduction;
        this.soldats = new ArrayList<>(); // Initialisation de la liste de soldats
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPointsProduction() {
        return pointsProduction;
    }

    public void setPointsProduction(int pointsProduction) {
        this.pointsProduction = pointsProduction;
    }

    public List<Soldat> getSoldats() {
        return soldats;
    }

    public void ajouterSoldat(Soldat soldat) {
        this.soldats.add(soldat); // Ajouter un soldat à la liste
    }

    public void retirerSoldat(Soldat soldat) {
        this.soldats.remove(soldat); // Retirer un soldat de la liste
    }

    // Methode pour verifier le nombre de ppoints de productions
    public boolean peutRecruterSoldat(int coutSoldat){
        return this.pointsProduction >= coutSoldat ;

    }

}


