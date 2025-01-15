package com.projet.game_4x.models;

import com.projet.game_4x.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public int calculerScore() {
        int score = 0;

        try (Connection connection = DBConnection.getConnection()) {
            // 1. Compter les territoires contrôlés (villes et tuiles)
            score += nombreTerritoire() * 10; // 10 points par territoire

            // 2. Compter les soldats possédés
            score += nombreSoldat() * 5; // 5 points par soldat

            /*
            // 3. Compter les victoires de combat
            String queryVictoires = "SELECT victoires_combat FROM joueurs WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(queryVictoires)) {
                stmt.setInt(1, this.id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    score += rs.getInt("victoires_combat") * 15; // 15 points par victoire
                }
            }*/

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du calcul du score.");
        }

        this.score = score; // Mettre à jour le score dans l'objet
        return score;
    }

    public int nombreSoldat() throws SQLException {
        Connection connection = DBConnection.getConnection();
        // 2. Compter les soldats possédés
        int result =0;
        String querySoldats = "SELECT COUNT(*) AS nb_soldats FROM soldats WHERE proprietaire_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(querySoldats)) {
            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("nb_soldats");
            }
        }
        return result;
    }


    public int nombreTerritoire() throws SQLException {
        Connection connection = DBConnection.getConnection();
        // 2. Compter les soldats possédés
        int result =0;
        String queryTerritoires = "SELECT COUNT(*) AS nb_territoires FROM tuiles WHERE proprietaire_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(queryTerritoires)) {
            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                result= rs.getInt("nb_territoires");
            }
        }
        return result;
    }

    public void sauvegarderScore() {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "UPDATE joueurs SET score = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, this.score);
                stmt.setInt(2, this.id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour du score.");
        }
    }

    public static Joueur getJoueurById(int id) {
        Joueur joueur = null;

        try (Connection connection = DBConnection.getConnection()) {
            String query = """
            SELECT id, login, mot_de_passe, score, points_de_production
            FROM joueurs
            WHERE id = ?
        """;
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                joueur = new Joueur(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("mot_de_passe"),
                        rs.getInt("score"),
                        rs.getInt("points_de_production")
                );
            } else {
                throw new RuntimeException("Joueur introuvable avec l'ID : " + id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération du joueur par ID.", e);
        }

        return joueur;
    }

    public static Joueur chargerSoldatJoueur(Joueur joueur) throws SQLException {
        // Charger les soldats du joueur
        String soldatsQuery = """
            SELECT s.id, s.position_tuile_id, t.x, t.y, s.points_de_vie, s.points_d_attaque, s.points_de_defense
            FROM soldats s
            JOIN tuiles t ON s.position_tuile_id = t.id
            WHERE s.proprietaire_id = ?
        """;
        Connection connection = DBConnection.getConnection();
        PreparedStatement soldatsStmt = connection.prepareStatement(soldatsQuery);
        soldatsStmt.setInt(1, joueur.getId());
        ResultSet soldatsResultSet = soldatsStmt.executeQuery();

        while (soldatsResultSet.next()) {
            // Créer chaque soldat
            Soldat soldat = new Soldat(
                    soldatsResultSet.getInt("id"),
                    joueur,
                    new Tuile(
                            soldatsResultSet.getInt("position_tuile_id"),
                            "vide", // Type de tuile par défaut (vous pouvez ajuster si nécessaire)
                            soldatsResultSet.getInt("x"),
                            soldatsResultSet.getInt("y"),
                            null,
                            0
                    ),
                    soldatsResultSet.getInt("points_de_vie"),
                    soldatsResultSet.getInt("points_d_attaque"),
                    soldatsResultSet.getInt("points_de_defense")
            );

            // Ajouter le soldat à la liste du joueur
            joueur.ajouterSoldat(soldat);
        }

        return joueur;
    }

}
