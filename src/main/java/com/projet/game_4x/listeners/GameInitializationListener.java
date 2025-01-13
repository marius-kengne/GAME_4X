package com.projet.game_4x.listeners;

import com.projet.game_4x.DAO.JoueurDAO;
import com.projet.game_4x.models.Carte;
import com.projet.game_4x.models.Joueur;
import com.projet.game_4x.utils.DBConnection;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebListener
public class GameInitializationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Créer une carte pour l'application
        Carte carte = new Carte(1, 10, 10);
        carte.genererCarteAleatoire();

        List<Joueur> joueurs = new ArrayList<>();

        // Charger les joueurs depuis la base de données
        try (Connection connection = DBConnection.getConnection()) {
            joueurs = JoueurDAO.getAllJoueurs(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Erreur lors de la récupération des joueurs depuis la base de données.");
        }

        // Stocker la carte et les joueurs dans le contexte de l'application
        sce.getServletContext().setAttribute("carte", carte);
        sce.getServletContext().setAttribute("joueurs", joueurs);
        sce.getServletContext().setAttribute("tourActuel", 0); // Le jeu commence avec le premier joueur
        System.out.println("Carte initialisée et joueurs chargés.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application arrêtée. Nettoyage des ressources...");
    }


}