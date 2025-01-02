package com.projet.game_4x.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Carte {
    private int id;
    private int largeur;
    private int hauteur;
    private List<Tuile> tuiles;

    public Carte(int id, int largeur, int hauteur) {
        this.id = id;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.tuiles = new ArrayList<>();
    }

    public void genererCarteAleatoire() {
        Random random = new Random();
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                String type;
                int rand = random.nextInt(100);

                if (rand < 10) {
                    type = "montagne";
                } else if (rand < 30) {
                    type = "foret";
                } else if (rand < 40) {
                    type = "ville";
                } else {
                    type = "vide";
                }

                Tuile tuile = new Tuile(0, type, x, y, null, type.equals("ville") ? 10 : 0);
                tuiles.add(tuile);
            }
        }
    }

    public Tuile getTuile(int x, int y) {
        return tuiles.stream()
                .filter(t -> t.getX() == x && t.getY() == y)
                .findFirst()
                .orElse(null);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLargeur() {
        return largeur;
    }

    public void setLargeur(int largeur) {
        this.largeur = largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public void setHauteur(int hauteur) {
        this.hauteur = hauteur;
    }

    public List<Tuile> getTuiles() {
        return tuiles;
    }

    public void setTuiles(List<Tuile> tuiles) {
        this.tuiles = tuiles;
    }
}
