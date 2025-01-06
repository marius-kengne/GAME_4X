package com.projet.game_4x.models;

import com.projet.game_4x.utils.DBConnection;

import java.sql.*;
import java.util.*;

public class Carte {
    private int id;
    private int largeur;
    private int hauteur;
    private List<Tuile> tuiles;
    private Map<String, Soldat> soldats; // Clé: "x_y", valeur: Soldat

    public Carte(int id, int largeur, int hauteur) {
        this.id = id;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.tuiles = new ArrayList<>();
        this.soldats = new HashMap<>();
    }

    // Charger les positions occupées et les associer aux tuiles
    private void chargerSoldatsEtPositions() {
        try (Connection connection = DBConnection.getConnection()) {
            String query = """
                SELECT s.id AS soldat_id, s.proprietaire_id, t.id AS tuile_id, t.x, t.y, j.login
                FROM soldats s
                JOIN tuiles t ON s.position_tuile_id = t.id
                JOIN joueurs j ON s.proprietaire_id = j.id
            """;
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                String positionKey = x + "_" + y;

                // Charger le joueur propriétaire
                Joueur proprietaire = new Joueur(
                        resultSet.getInt("proprietaire_id"),
                        resultSet.getString("login"),
                        "", // Mot de passe inutile ici
                        0,  // Score par défaut
                        0   // Points de production par défaut
                );

                // Créer le soldat
                Soldat soldat = new Soldat(
                        resultSet.getInt("soldat_id"),
                        proprietaire,
                        null, // La tuile sera associée plus tard
                        100,  // Points de vie
                        10,   // Points d'attaque
                        5     // Points de défense
                );

                // Ajouter le soldat au map
                soldats.put(positionKey, soldat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement des soldats.");
        }
    }

    // Générer une carte aléatoire en respectant les soldats existants
    public void genererCarteAleatoire() {
        chargerSoldatsEtPositions(); // Charger les soldats et leurs positions

        Random random = new Random();
        for (int x = 0; x < largeur; x++) {
            for (int y = 0; y < hauteur; y++) {
                String positionKey = x + "_" + y;

                // Si la position est occupée par un soldat, créer une tuile avec ce soldat
                if (soldats.containsKey(positionKey)) {
                    Tuile tuile = new Tuile(0, "vide", x, y, null, 0);
                    Soldat soldat = soldats.get(positionKey);
                    tuile.setSoldat(soldat);
                    soldat.setPosition(tuile); // Associer la tuile au soldat
                    tuiles.add(tuile);
                    continue;
                }

                // Générer une tuile aléatoire pour les positions libres
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

    // Accesseurs et mutateurs
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carte ID: ").append(id).append("\n");
        sb.append("Dimensions: ").append(largeur).append("x").append(hauteur).append("\n");
        sb.append("Grille:\n");

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                Tuile tuile = getTuile(x, y);
                if (tuile != null) {
                    switch (tuile.getType()) {
                        case "montagne":
                            sb.append("M ");
                            break;
                        case "foret":
                            sb.append("F ");
                            break;
                        case "ville":
                            sb.append("V ");
                            break;
                        case "vide":
                        default:
                            sb.append(". ");
                            break;
                    }
                } else {
                    sb.append("? ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
