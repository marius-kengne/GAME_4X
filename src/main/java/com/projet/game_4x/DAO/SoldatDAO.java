package com.projet.game_4x.DAO;

import com.projet.game_4x.models.Soldat;
import com.projet.game_4x.models.Tuile;
import com.projet.game_4x.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SoldatDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/game_4x";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private void supprimerSoldat(Connection connection, Soldat soldat) throws SQLException {
        String query = "DELETE FROM soldats WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, soldat.getId());
            stmt.executeUpdate();
        }
    }

    private void updateTuileProprietaire(Tuile tuile) {
        if (tuile.getProprietaire() == null) {
            throw new IllegalArgumentException("La tuile doit avoir un propriétaire valide.");
        }

        try (Connection connection = DBConnection.getConnection()) {
            String query = "UPDATE tuiles SET proprietaire_id = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, tuile.getProprietaire().getId());
            stmt.setInt(2, tuile.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour du propriétaire de la tuile.", e);
        }
    }


}
