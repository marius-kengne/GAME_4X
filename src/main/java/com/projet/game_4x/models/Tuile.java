package com.projet.game_4x.models;

public class Tuile {
    private int id;
    private String type; // "ville", "montagne", "foret", "vide"
    private int x;
    private int y;
    private Joueur proprietaire; // null si aucun joueur
    private int pointsDeDefense; // Pour les villes uniquement
    private Soldat soldat; // Nouveau champ pour gérer les soldats

    public Tuile(int id, String type, int x, int y, Joueur proprietaire, int pointsDeDefense) {
        this.id = id;
        this.type = type;
        this.x = x;
        this.y = y;
        this.proprietaire = proprietaire;
        this.pointsDeDefense = pointsDeDefense;
        this.soldat = null; // Par défaut, aucune tuile ne contient de soldat
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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

    public Soldat getSoldat() {
        return soldat;
    }

    public void setSoldat(Soldat soldat) {
        this.soldat = soldat;
    }
}
