package com.projet.game_4x.listeners;

import com.projet.game_4x.utils.GameWebSocket;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class GameInitializationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        /*
        // Charger ou générer une carte unique pour l'application
        Carte carte = Carte.chargerOuGenererCarte(10, 10);

        // Stocker la carte dans le contexte de l'application
        sce.getServletContext().setAttribute("carte", carte);
        sce.getServletContext().setAttribute("carteId", carte.getId());
        sce.getServletContext().setAttribute("tourActuel", 0);

        System.out.println("Carte initialisée et stockée dans le contexte de l'application.");
        System.out.println("Carte aléatoire initialisée avec succès (ID : " + carte.getId() + ").");
        */
        System.out.println("###### passage dans GameInitializationListener");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nettoyage des ressources si nécessaire
        System.out.println("Application arrêtée. Nettoyage des ressources...");
        GameWebSocket.clearChat();
    }
}