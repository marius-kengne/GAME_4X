package com.projet.game_4x.utils;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/gameUpdates/{playerId}")
public class GameWebSocket {

    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

    /**
     * Quand un client se connecte, ajoutez la session à la map avec un identifiant.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("playerId") String playerId) {
        sessions.put(playerId, session);
        System.out.println("Client connecté : " + playerId);
    }

    /**
     * Quand un client se déconnecte, supprimez la session de la map.
     */
    @OnClose
    public void onClose(Session session, @PathParam("playerId") String playerId) {
        sessions.remove(playerId);
        System.out.println("Client déconnecté : " + playerId);
    }

    /**
     * Gérer les erreurs de WebSocket.
     */
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("Erreur WebSocket : " + throwable.getMessage());
    }

    /**
     * Réception d’un message d’un client.
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message reçu : " + message);
    }

    /**
     * Envoyer un message à un client spécifique.
     */
    public static void sendToClient(String playerId, String message) {
        Session session = sessions.get(playerId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi du message au client " + playerId + " : " + e.getMessage());
            }
        } else {
            System.out.println("La session pour le client " + playerId + " n'est pas disponible.");
        }
    }

    /**
     * Envoyer un message à tous les clients connectés.
     */
    public static void broadcast(String message) {
        for (Map.Entry<String, Session> entry : sessions.entrySet()) {
            Session session = entry.getValue();
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'envoi du message global : " + e.getMessage());
                }
            }
        }
    }
}
