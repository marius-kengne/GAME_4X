package com.projet.game_4x.models;

public class Joueur {
    private int id;
    private String login;
    private String motDePasse;
    private int score;
    private int pointsProduction;

    public Joueur(int id, String login, String motDePasse, int score, int pointsProduction) {
        this.id = id;
        this.login = login;
        this.motDePasse = motDePasse;
        this.score = score;
        this.pointsProduction = pointsProduction;
    }

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
}
