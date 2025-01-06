package com.projet.game_4x.listeners;

import com.projet.game_4x.models.Carte;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class GameInitializationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Créer une carte unique pour l'application
        Carte carte = new Carte(1, 10, 10);
        carte.genererCarteAleatoire();

        // Stocker la carte dans le contexte de l'application
        sce.getServletContext().setAttribute("carte", carte);
        sce.getServletContext().setAttribute("tourActuel", 0);

        System.out.println("Carte initialisée et stockée dans le contexte de l'application.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nettoyage des ressources si nécessaire
        System.out.println("Application arrêtée. Nettoyage des ressources...");
    }
}
