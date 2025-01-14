package com.projet.game_4x.models;

import com.projet.game_4x.utils.DBConnection;

import java.sql.*;
import java.util.*;

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

    // Méthode pour générer une carte si aucune carte n'existe dans la BD
    public static Carte chargerOuGenererCarte(int largeur, int hauteur) {
        try (Connection connection = DBConnection.getConnection()) {
            // Vérifier si une carte existe déjà
            String queryCheck = "SELECT id, largeur, hauteur FROM cartes LIMIT 1";
            PreparedStatement stmtCheck = connection.prepareStatement(queryCheck);
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) {
                // Charger la carte existante
                int carteId = rs.getInt("id");
                int existingLargeur = rs.getInt("largeur");
                int existingHauteur = rs.getInt("hauteur");

                Carte carte = new Carte(carteId, existingLargeur, existingHauteur);
                carte.chargerTuilesEtSoldatsDepuisBD(); // Charger les tuiles et les soldats associés
                System.out.println("Carte existante chargée depuis la base de données.");
                return carte;
            } else {
                // Créer une nouvelle carte si aucune n'existe
                Carte nouvelleCarte = new Carte(0, largeur, hauteur);
                nouvelleCarte.sauvegarderDansBD(connection);
                System.out.println("Nouvelle carte générée et sauvegardée dans la base de données.");
                return nouvelleCarte;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement ou de la génération de la carte.");
        }
    }


    private void sauvegarderDansBD(Connection connection) throws SQLException {
        // Insérer la carte dans la table
        String insertCarteQuery = "INSERT INTO cartes (largeur, hauteur) VALUES (?, ?)";
        PreparedStatement stmtCarte = connection.prepareStatement(insertCarteQuery, Statement.RETURN_GENERATED_KEYS);
        stmtCarte.setInt(1, this.largeur);
        stmtCarte.setInt(2, this.hauteur);
        stmtCarte.executeUpdate();

        ResultSet generatedKeys = stmtCarte.getGeneratedKeys();
        if (generatedKeys.next()) {
            this.id = generatedKeys.getInt(1);
        }

        // Générer et insérer les tuiles
        String insertTuileQuery = "INSERT INTO tuiles (carte_id, type, x, y, points_de_defense) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmtTuile = connection.prepareStatement(insertTuileQuery);

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

                int pointsDeDefense = "ville".equals(type) ? 10 : 0;

                stmtTuile.setInt(1, this.id);
                stmtTuile.setString(2, type);
                stmtTuile.setInt(3, x);
                stmtTuile.setInt(4, y);
                stmtTuile.setInt(5, pointsDeDefense);
                stmtTuile.addBatch();
            }
        }
        stmtTuile.executeBatch();
    }

    private void chargerTuilesEtSoldatsDepuisBD() {
        try (Connection connection = DBConnection.getConnection()) {
            // Charger les tuiles
            String queryTuiles = "SELECT * FROM tuiles WHERE carte_id = ?";
            PreparedStatement stmtTuiles = connection.prepareStatement(queryTuiles);
            stmtTuiles.setInt(1, this.id);
            ResultSet rsTuiles = stmtTuiles.executeQuery();

            while (rsTuiles.next()) {
                Tuile tuile = new Tuile(
                        rsTuiles.getInt("id"),
                        rsTuiles.getString("type"),
                        rsTuiles.getInt("x"),
                        rsTuiles.getInt("y"),
                        null,
                        rsTuiles.getInt("points_de_defense")
                );
                this.tuiles.add(tuile);
            }

            // Charger les soldats et les assigner aux tuiles
            String querySoldats = """
                SELECT s.id AS soldat_id, s.proprietaire_id, t.id AS tuile_id, t.x, t.y, j.login,
                       s.points_de_vie, s.points_d_attaque, s.points_de_defense
                FROM soldats s
                JOIN tuiles t ON s.position_tuile_id = t.id
                JOIN joueurs j ON s.proprietaire_id = j.id
                WHERE t.carte_id = ?
            """;
            PreparedStatement stmtSoldats = connection.prepareStatement(querySoldats);
            stmtSoldats.setInt(1, this.id);
            ResultSet rsSoldats = stmtSoldats.executeQuery();

            while (rsSoldats.next()) {
                Tuile tuile = getTuile(rsSoldats.getInt("x"), rsSoldats.getInt("y"));
                if (tuile != null) {
                    Joueur proprietaire = new Joueur(
                            rsSoldats.getInt("proprietaire_id"),
                            rsSoldats.getString("login"),
                            "",
                            0,
                            0
                    );

                    Soldat soldat = new Soldat(
                            rsSoldats.getInt("soldat_id"),
                            proprietaire,
                            tuile,
                            rsSoldats.getInt("points_de_vie"),
                            rsSoldats.getInt("points_d_attaque"),
                            rsSoldats.getInt("points_de_defense")
                    );

                    tuile.setSoldat(soldat);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement des tuiles et des soldats pour la carte.");
        }
    }

    public Tuile getTuile(int x, int y) {
        return tuiles.stream()
                .filter(t -> t.getX() == x && t.getY() == y)
                .findFirst()
                .orElse(null);
    }

    public List<Tuile> getTuiles() {
        return tuiles;
    }

    public int getId() {
        return id;
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public Tuile getAdjacentTuile(Tuile current, String direction) {
        if (direction == null) {
            throw new IllegalArgumentException("La direction ne peut pas être null.");
        }

        switch (direction) {
            case "moveNorth":
                return getTuile(current.getX(), current.getY() - 1);
            case "moveSouth":
                return getTuile(current.getX(), current.getY() + 1);
            case "moveEast":
                return getTuile(current.getX() + 1, current.getY());
            case "moveWest":
                return getTuile(current.getX() - 1, current.getY());
            default:
                throw new IllegalArgumentException("Direction invalide : " + direction);
        }
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

    public static Carte chargerCarteExistante(int id) {
        try (Connection connection = DBConnection.getConnection()) {
            // Préparer la requête pour vérifier et charger une carte existante
            String queryCheck = "SELECT id, largeur, hauteur FROM cartes WHERE id = ? LIMIT 1";
            PreparedStatement stmtCheck = connection.prepareStatement(queryCheck);
            stmtCheck.setInt(1, id); // Passer l'ID de la carte en paramètre
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) { // Vérifier si une carte correspond à l'ID
                // Charger les informations de la carte
                int carteId = rs.getInt("id");
                int existingLargeur = rs.getInt("largeur");
                int existingHauteur = rs.getInt("hauteur");

                // Créer une instance de la carte et charger les tuiles/soldats
                Carte carte = new Carte(carteId, existingLargeur, existingHauteur);
                carte.chargerTuilesEtSoldatsDepuisBD(); // Charger les tuiles et soldats associés
                System.out.println("Carte existante chargée depuis la base de données.");
                return carte;
            } else {
                throw new RuntimeException("Aucune carte trouvée avec l'ID : " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement de la carte avec l'ID : " + id, e);
        }
    }
}