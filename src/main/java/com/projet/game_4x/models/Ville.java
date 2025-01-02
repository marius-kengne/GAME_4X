package com.projet.game_4x.models;

public class Ville {
    private int id;
    private Joueur proprietaire;
    private int pointsDeDefense;
    private int pointsDeProduction;

    public Ville(int id, Joueur proprietaire, int pointsDeDefense, int pointsDeProduction) {
        this.id = id;
        this.proprietaire = proprietaire;
        this.pointsDeDefense = pointsDeDefense;
        this.pointsDeProduction = pointsDeProduction;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Joueur getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Joueur proprietaire) {
        this.proprietaire = proprietaire;
    }

    public int getPointsDeDefense() {
        return pointsDeDefense;
    }

    public void setPointsDeDefense(int pointsDeDefense) {
        this.pointsDeDefense = pointsDeDefense;
    }

    public int getPointsDeProduction() {
        return pointsDeProduction;
    }

    public void setPointsDeProduction(int pointsDeProduction) {
        this.pointsDeProduction = pointsDeProduction;
    }
}
