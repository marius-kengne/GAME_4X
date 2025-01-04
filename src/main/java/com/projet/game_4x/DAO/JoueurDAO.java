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

    public static Joueur authenticateJoueur(String login, String mot_de_passe) {

        String query = "SELECT * FROM joueurs WHERE login = ?";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e){
            e.printStackTrace();
            System.out.println("class driver not found");
        }

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, login);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Récupération du mot de passe hashé dans la BD
                    String storedPassword = resultSet.getString("mot_de_passe");

                    // Comparaison des mots de passe (appliquez le hash si nécessaire)
                    if (mot_de_passe.equals(storedPassword)) {
                        System.out.println("le joueur est retourner avec succes -- la connexion a reussi");
                        return new Joueur(
                                resultSet.getInt("id"),
                                resultSet.getString("login"),
                                resultSet.getString("mot_de_passe"),
                                resultSet.getInt("score"),
                                resultSet.getInt("points_de_production")
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Retourne null si les crédentials sont invalides
    }
}
