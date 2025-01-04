package com.projet.game_4x.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtils {
    // URL de connexion à la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/game_4x?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";  // Remplacez par votre utilisateur MySQL
    private static final String PASSWORD = " ";  // Remplacez par votre mot de passe MySQL

    // Méthode pour établir la connexion
    public static Connection getConnection() throws SQLException {
        try {
            // Chargement explicite du driver JDBC MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver JDBC MySQL chargé avec succès.");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC MySQL introuvable. Vérifiez la dépendance MySQL dans pom.xml", e);
        }

        // Établir la connexion avec les paramètres spécifiés
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("Connexion à la base de données établie avec succès.");
        return connection;
    }
}