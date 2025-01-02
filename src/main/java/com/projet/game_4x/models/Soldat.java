package com.projet.game_4x.models;

public class Soldat {
    private int id;
    private Joueur proprietaire;
    private Tuile position;
    private int pointsDeVie;
    private int pointsDAttaque;
    private int pointsDeDefense;

    public Soldat(int id, Joueur proprietaire, Tuile position, int pointsDeVie, int pointsDAttaque, int pointsDeDefense) {
        this.id = id;
        this.proprietaire = proprietaire;
        this.position = position;
        this.pointsDeVie = pointsDeVie;
        this.pointsDAttaque = pointsDAttaque;
        this.pointsDeDefense = pointsDeDefense;
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

    public Tuile getPosition() {
        return position;
    }

    public void setPosition(Tuile position) {
        this.position = position;
    }

    public int getPointsDeVie() {
        return pointsDeVie;
    }

    public void setPointsDeVie(int pointsDeVie) {
        this.pointsDeVie = pointsDeVie;
    }

    public int getPointsDAttaque() {
        return pointsDAttaque;
    }

    public void setPointsDAttaque(int pointsDAttaque) {
        this.pointsDAttaque = pointsDAttaque;
    }

    public int getPointsDeDefense() {
        return pointsDeDefense;
    }

    public void setPointsDeDefense(int pointsDeDefense) {
        this.pointsDeDefense = pointsDeDefense;
    }
}
