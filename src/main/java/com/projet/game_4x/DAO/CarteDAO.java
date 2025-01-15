package com.projet.game_4x.DAO;

import com.projet.game_4x.models.Joueur;
import com.projet.game_4x.models.Soldat;
import com.projet.game_4x.models.Tuile;
import com.projet.game_4x.utils.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CarteDAO {
    private static final String URL = "jdbc:mysql://localhost:3306/game_4x";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private int id;
    private List<Tuile> tuiles;


    private void chargerTuilesEtSoldatsDepuisBDOld() {
        try (Connection connection = DBConnection.getConnection()) {
            // Charger les tuiles
            String queryTuiles = "SELECT * FROM tuiles WHERE carte_id = ?";
            PreparedStatement stmtTuiles = connection.prepareStatement(queryTuiles);
            stmtTuiles.setInt(1, this.id);
            ResultSet rsTuiles = stmtTuiles.executeQuery();
            Tuile tuile = null;
            while (rsTuiles.next()) {
                tuile = new Tuile(
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
                //tuile = Tuile.getTuile(rsSoldats.getInt("x"), rsSoldats.getInt("y"));
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
}
