package com.projet.game_4x.DAO;

import com.projet.game_4x.models.Soldat;
import com.projet.game_4x.models.Tuile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TuileDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/game_4x";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private void updateSoldat(Connection connection, Soldat soldat) throws SQLException {
        String query = "UPDATE soldats SET points_de_vie = ?, points_de_defense = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, soldat.getPointsDeVie());
            stmt.setInt(2, soldat.getPointsDeDefense());
            stmt.setInt(3, soldat.getId());
            stmt.executeUpdate();
        }
    }

    private void updateTuile(Connection connection, Tuile tuile) throws SQLException {
        String query = "UPDATE tuiles SET points_de_defense = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, tuile.getPointsDeDefense());
            stmt.setInt(2, tuile.getId());
            stmt.executeUpdate();
        }
    }

    private void updateTuileProprietaire(Connection connection, Tuile tuile) throws SQLException {
        String query = "UPDATE tuiles SET proprietaire_id = ?, points_de_defense = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, tuile.getProprietaire().getId());
            stmt.setInt(2, 0); // Défense mise à zéro après capture
            stmt.setInt(3, tuile.getId());
            stmt.executeUpdate();
        }
    }
}
