package com.projet.game_4x.DAO;
import com.projet.game_4x.utils.DBConnection;
import com.projet.game_4x.models.Joueur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JoueurDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/game_4x";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    // Méthode pour inscrire un joueur dans la base de données
    public static boolean registerUsers(Joueur joueur) {
        String query = "INSERT INTO joueurs (login, mot_de_passe) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, joueur.getLogin());
            statement.setString(2, joueur.getMotDePasse());
            return statement.executeUpdate() > 0; // Retourne true si l'insertion réussit
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Méthode pour authentifier un joueur avec une connexion existante
    public static Joueur authenticateJoueur(Connection connection, String login, String password) throws SQLException {
        String query = "SELECT id, login, score, points_de_production FROM joueurs WHERE login = ? AND mot_de_passe = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, password);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new Joueur(
                            resultSet.getInt("id"),
                            resultSet.getString("login"),
                            password,
                            resultSet.getInt("score"),
                            resultSet.getInt("points_de_production")
                    );
                }
            }
        }
        return null; // Retourne null si l'utilisateur n'est pas trouvé
    }

    // Méthode pour récupérer tous les joueurs
    public static List<Joueur> getAllJoueurs(Connection connection) throws SQLException {
        List<Joueur> joueurs = new ArrayList<>();
        String query = "SELECT id, login, mot_de_passe, score, points_de_production FROM joueurs";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Joueur joueur = new Joueur(
                        rs.getInt("id"),
                        rs.getString("login"),
                        rs.getString("mot_de_passe"),
                        rs.getInt("score"),
                        rs.getInt("points_de_production")
                );
                joueurs.add(joueur);
            }
        }
        return joueurs;
    }

    // Méthode pour récupérer le score d'un joueur
    public static int getScore(Connection connection, int joueurId) throws SQLException {
        String query = "SELECT score FROM joueurs WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, joueurId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("score");
                }
            }
        }
        return 0;
    }

    // Méthode pour mettre à jour le score d'un joueur
    public static boolean updateScore(Connection connection, int joueurId, int points) throws SQLException {
        String query = "UPDATE joueurs SET score = score + ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, points);
            stmt.setInt(2, joueurId);
            return stmt.executeUpdate() > 0; // True si la mise à jour a réussi
        }
    }

    // Méthode pour mettre à jour les points de production après le recrutement
    public static void updatePointsProduction(Connection connection, int joueurId, int newPointsProduction) throws SQLException {
        String query = "UPDATE joueurs SET points_de_production = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newPointsProduction);
            stmt.setInt(2, joueurId);
            stmt.executeUpdate();
        }
    }

    // Méthode pour vérifier si un login est déjà utilisé
    public static boolean isLoginUsed(Connection connection, String login) throws SQLException {
        String query = "SELECT 1 FROM joueurs WHERE login = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retourne true si le login existe
            }
        }
    }

    // Méthode pour recruter un soldat
    public static boolean recruterSoldat(Connection connection, int joueurId, int positionTuileId) throws SQLException {
        String insertQuery = "INSERT INTO soldats (proprietaire_id, position_tuile_id) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, joueurId);
            stmt.setInt(2, positionTuileId);
            return stmt.executeUpdate() > 0; // True si l'insertion réussit
        }
    }

    // Méthode pour récupérer une tuile vide pour le recrutement
    public static int getRandomEmptyTuile(Connection connection, int carteId) throws SQLException {
        String query = """
                SELECT id 
                FROM tuiles 
                WHERE carte_id = ? 
                AND proprietaire_id IS NULL 
                AND type = 'vide' 
                ORDER BY RAND() 
                LIMIT 1
                """;
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, carteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Retourne -1 si aucune tuile vide n'est trouvée
    }
}