package com.projet.game_4x.DAO;

import com.oracle.wls.shaded.org.apache.bcel.generic.PUSH;
import com.projet.game_4x.models.Joueur;

import java.sql.*;

public class JoueurDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/game_4x";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static boolean registerUsers(Joueur joueur){

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

    public static Joueur authenticateJoueur(Connection connection, String login, String password) throws SQLException{

        String query = "SELECT id, score, points_de_production FROM joueurs WHERE login = ? AND mot_de_passe = ?";
        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setString(1, login);
        stmt.setString(2, password);

        ResultSet resultSet = stmt.executeQuery();

        if (resultSet.next()) {
            return new Joueur(
                    resultSet.getInt("id"),
                    login,
                    password,
                    resultSet.getInt("score"),
                    resultSet.getInt("points_de_production")
            );
        }
        return null;
    }


    public static String inscrireJoueur(Connection connection, String login, String password, Object carteIdObj) throws SQLException {
        // Vérification si le login existe déjà
        String checkQuery = "SELECT id FROM joueurs WHERE login = ?";
        PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
        checkStmt.setString(1, login);
        ResultSet resultSet = checkStmt.executeQuery();
        if (resultSet.next()) {
            return "Le login est déjà utilisé.";
        }

        // Création du joueur
        String insertJoueurQuery = "INSERT INTO joueurs (login, mot_de_passe) VALUES (?, ?)";
        PreparedStatement insertJoueurStmt = connection.prepareStatement(insertJoueurQuery, Statement.RETURN_GENERATED_KEYS);
        insertJoueurStmt.setString(1, login);
        insertJoueurStmt.setString(2, password);
        insertJoueurStmt.executeUpdate();

        // Récupérer l'ID du joueur
        ResultSet generatedKeys = insertJoueurStmt.getGeneratedKeys();
        if (!generatedKeys.next()) {
            return "Erreur lors de la création du joueur.";
        }
        int joueurId = generatedKeys.getInt(1);

        // Récupération de l'ID de la carte
        if (carteIdObj == null) {
            return "La carte n'a pas été initialisée.";
        }
        int carteId = (int) carteIdObj;

        // Trouver une tuile vide dans la plage ID 1-24
        String findTuileQuery = """
            SELECT id 
            FROM tuiles 
            WHERE carte_id = ? 
            AND type = 'vide' 
            AND proprietaire_id IS NULL 
            AND id BETWEEN 1 AND 99 
            LIMIT 1
        """;
        PreparedStatement findTuileStmt = connection.prepareStatement(findTuileQuery);
        findTuileStmt.setInt(1, carteId);
        ResultSet tuileResult = findTuileStmt.executeQuery();

        if (!tuileResult.next()) {
            return "Aucune tuile disponible pour le joueur dans la plage 1-99.";
        }
        int tuileId = tuileResult.getInt("id");

        // Associer la tuile au joueur
        String updateTuileQuery = "UPDATE tuiles SET proprietaire_id = ? WHERE id = ?";
        PreparedStatement updateTuileStmt = connection.prepareStatement(updateTuileQuery);
        updateTuileStmt.setInt(1, joueurId);
        updateTuileStmt.setInt(2, tuileId);
        updateTuileStmt.executeUpdate();

        // Créer un soldat sur la tuile
        String insertSoldatQuery = "INSERT INTO soldats (proprietaire_id, position_tuile_id) VALUES (?, ?)";
        PreparedStatement insertSoldatStmt = connection.prepareStatement(insertSoldatQuery);
        insertSoldatStmt.setInt(1, joueurId);
        insertSoldatStmt.setInt(2, tuileId);
        insertSoldatStmt.executeUpdate();

        return "success";
    }

}
