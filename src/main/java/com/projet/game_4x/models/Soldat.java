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

    // Getters et Setters
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

    // Méthode pour soigner le soldat
    public void soigner(int points) {
        this.pointsDeVie = Math.min(this.pointsDeVie + points, 10); // Limite de points de vie à 10
    }

    // Méthode pour attaquer un autre soldat
    public int attaquer(Soldat cible) {
        int degats = Math.max(this.pointsDAttaque - cible.getPointsDeDefense(), 0); // Dégâts effectifs
        cible.setPointsDeVie(cible.getPointsDeVie() - degats); // Réduire les points de vie de la cible
        cible.setPointsDeDefense(cible.getPointsDeDefense() - degats);
        return degats;
    }
}
