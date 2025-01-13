package com.projet.game_4x.models;

public class Foret {
    private int id;
    private int quantiteRessources;

    public Foret(int id, int quantiteRessources) {
        this.id = id;
        this.quantiteRessources = quantiteRessources;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuantiteRessources() {
        return quantiteRessources;
    }

    public void setQuantiteRessources(int quantiteRessources) {
        this.quantiteRessources = quantiteRessources;
    }
}
