package com.projet.game_4x.DAO;

import com.projet.game_4x.models.Carte;
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

    public static Carte chargerCarteExistante(int id) {
        try (Connection connection = DBConnection.getConnection()) {
            // Préparer la requête pour vérifier et charger une carte existante
            String queryCheck = "SELECT id, largeur, hauteur FROM cartes WHERE id = ? LIMIT 1";
            PreparedStatement stmtCheck = connection.prepareStatement(queryCheck);
            stmtCheck.setInt(1, id); // Passer l'ID de la carte en paramètre
            ResultSet rs = stmtCheck.executeQuery();

            if (rs.next()) { // Vérifier si une carte correspond à l'ID
                // Charger les informations de la carte
                int carteId = rs.getInt("id");
                int existingLargeur = rs.getInt("largeur");
                int existingHauteur = rs.getInt("hauteur");

                // Créer une instance de la carte et charger les tuiles/soldats
                Carte carte = new Carte(carteId, existingLargeur, existingHauteur);
                carte.chargerTuilesEtSoldatsDepuisBD(); // Charger les tuiles et soldats associés
                System.out.println("Carte existante chargée depuis la base de données.");
                return carte;
            } else {
                throw new RuntimeException("Aucune carte trouvée avec l'ID : " + id);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors du chargement de la carte avec l'ID : " + id, e);
        }
    }
}
