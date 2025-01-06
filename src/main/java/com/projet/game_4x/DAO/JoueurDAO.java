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
}
