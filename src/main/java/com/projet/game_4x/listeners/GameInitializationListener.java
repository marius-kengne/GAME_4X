package com.projet.game_4x.listeners;

import com.projet.game_4x.utils.GameWebSocket;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class GameInitializationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("###### passage dans GameInitializationListener");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Application arrêtée. Nettoyage des ressources...");
        GameWebSocket.clearChat();
    }
}