package com.projet.game_4x.utils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

@WebListener
public class GameInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection connection = DBConnection.getConnection()) {
            // Créer une nouvelle carte aléatoire
            String createCarteQuery = "INSERT INTO cartes (largeur, hauteur) VALUES (?, ?)";
            PreparedStatement createCarteStmt = connection.prepareStatement(createCarteQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            createCarteStmt.setInt(1, 10); // Largeur de la carte
            createCarteStmt.setInt(2, 10); // Hauteur de la carte
            createCarteStmt.executeUpdate();

            // Récupérer l'ID de la carte
            ResultSet generatedKeys = createCarteStmt.getGeneratedKeys();
            int carteId = -1;
            if (generatedKeys.next()) {
                carteId = generatedKeys.getInt(1);
            }

            if (carteId != -1) {
                // Générer les tuiles pour cette carte
                Random random = new Random();
                for (int x = 0; x < 10; x++) {
                    for (int y = 0; y < 10; y++) {
                        String type;
                        int rand = random.nextInt(100);

                        if (rand < 10) {
                            type = "montagne";
                        } else if (rand < 30) {
                            type = "foret";
                        } else if (rand < 40) {
                            type = "ville";
                        } else {
                            type = "vide";
                        }

                        String createTuileQuery = "INSERT INTO tuiles (carte_id, type, x, y) VALUES (?, ?, ?, ?)";
                        PreparedStatement createTuileStmt = connection.prepareStatement(createTuileQuery);
                        createTuileStmt.setInt(1, carteId);
                        createTuileStmt.setString(2, type);
                        createTuileStmt.setInt(3, x);
                        createTuileStmt.setInt(4, y);
                        createTuileStmt.executeUpdate();
                    }
                }
            }

            // Stocker l'ID de la carte dans le contexte de l'application
            sce.getServletContext().setAttribute("carteId", carteId);

            System.out.println("Carte aléatoire initialisée avec succès (ID : " + carteId + ").");

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation de la carte : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Rien à faire lors de l'arrêt de l'application
        System.out.println("Application arrêtée.");
    }
}