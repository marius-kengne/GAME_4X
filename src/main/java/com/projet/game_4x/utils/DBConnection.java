package com.projet.game_4x.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/game_4x";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Enregistrement explicite du driver
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver MySQL introuvable !", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
