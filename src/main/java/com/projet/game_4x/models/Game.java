package com.projet.game_4x.models;

import com.projet.game_4x.utils.DBConnection;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Game {

    private static Game instance; // Singleton instance
    private Carte carte;
    private List<Integer> joueurs; // Liste des IDs des joueurs connectés
    private int currentPlayer; // Joueur actuel
    private boolean start = false; //status du game start ou end

    private Game() {
        this.joueurs = new ArrayList<>();
        //this.currentPlayer = 1; // Le joueur 1 commence par défaut
    }

    public static synchronized Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public Carte getCarte() {
        return carte;
    }

    public void setCarte(Carte carte) {
        this.carte = carte;
    }

    public List<Integer> getJoueurs() {
        return joueurs;
    }

    public void addJoueur(int joueurId) {
        if (!joueurs.contains(joueurId)) {
            joueurs.add(joueurId);
        }
    }

    public synchronized int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }


    public synchronized void nextPlayerOld() {
        int totalPlayers = joueurs.size();
        currentPlayer = (currentPlayer % totalPlayers) + 1;
    }

    public synchronized void nextPlayer() {

        if (joueurs.isEmpty()) {
            throw new IllegalStateException("La liste des joueurs est vide. Impossible de définir le prochain joueur.");
        }

        // Charger l'objet Joueur actuel depuis la base
        Joueur joueurActuel = Joueur.getJoueurById(currentPlayer);

        // Trouver l'index du joueur actuel dans la liste
        int currentIndex = joueurs.indexOf(currentPlayer);

        // Si le joueur actuel n'est pas trouvé (par exemple, première exécution), commencer par le premier joueur
        if (currentIndex == -1) {
            currentPlayer = joueurs.get(0); // Définir le premier joueur comme joueur actuel
        } else {
            // Calculer l'index du prochain joueur
            int nextIndex = (currentIndex + 1) % joueurs.size();
            currentPlayer = joueurs.get(nextIndex); // Obtenir l'ID du prochain joueur
        }


        System.out.println("********* Valeur actuelle des points : " + joueurActuel.getPointsProduction());

        // Produire les points de production pour le joueur
        //produirePointsPourJoueur(joueurActuel);
        ajouterPointsDeProductionAuJoueur(joueurActuel,5);

        // Recharger la carte pour s'assurer qu'elle reflète l'état actuel
        //this.carte = carte.chargerOuGenererCarte(10,10);

        // Recharger les données du joueur pour voir les nouvelles valeurs
        joueurActuel = Joueur.getJoueurById(currentPlayer);

        System.out.println("********* Nouvelle valeur des points : " + joueurActuel.getPointsProduction());
    }


    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public void produirePointsParTour() throws SQLException {
        try (Connection connection = DBConnection.getConnection()) {
            // Récupérer toutes les villes avec leur propriétaire
            String query = "SELECT v.id, v.points_de_production, v.proprietaire_id FROM villes v WHERE v.proprietaire_id IS NOT NULL";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int villeId = rs.getInt("id");
                int pointsProduction = rs.getInt("points_de_production");
                int proprietaireId = rs.getInt("proprietaire_id");

                // Ajouter les points de production au joueur propriétaire
                String updateJoueur = "UPDATE joueurs SET points_de_production = points_de_production + ? WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateJoueur)) {
                    updateStmt.setInt(1, pointsProduction);
                    updateStmt.setInt(2, proprietaireId);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la production de points par les villes.");
        }
    }

    public void produirePointsPourJoueur(Joueur joueur) {
        System.out.println("************* Production de points pour le joueur : " + joueur.getLogin());
        try (Connection connection = DBConnection.getConnection()) {
            // Récupérer toutes les tuiles de type 'ville' appartenant au joueur
            String query = """
            SELECT t.points_de_defense AS points_de_production
            FROM tuiles t
            WHERE t.type = 'ville' AND t.proprietaire_id = ?
        """;
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, joueur.getId());
            ResultSet rs = stmt.executeQuery();

            int totalPoints = 0;

            // Calculer le total des points de production des villes du joueur
            while (rs.next()) {
                totalPoints += rs.getInt("points_de_production");
            }

            if (totalPoints > 0) {
                // Ajouter les points de production au joueur
                String updateQuery = "UPDATE joueurs SET points_de_production = points_de_production + ? WHERE id = ?";
                PreparedStatement updateStmt = connection.prepareStatement(updateQuery);
                updateStmt.setInt(1, totalPoints);
                updateStmt.setInt(2, joueur.getId());
                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Les points de production du joueur ont été mis à jour : +" + totalPoints);
                } else {
                    System.out.println("Aucune mise à jour effectuée pour les points de production du joueur.");
                }
            } else {
                System.out.println("Aucune production à ajouter pour le joueur : " + joueur.getLogin());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la production des points pour le joueur.", e);
        }
    }


    public static void ajouterPointsDeProductionAuJoueur(Joueur joueur, int points) {
        System.out.println("********* Valeur actuelle des points : " + joueur.getPointsProduction());
        try (Connection connection = DBConnection.getConnection()) {
            // Requête pour ajouter des points de production
            String updateQuery = "UPDATE joueurs SET points_de_production = points_de_production + ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(updateQuery);
            stmt.setInt(1, points);
            stmt.setInt(2, joueur.getId());

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Les points de production ont été augmentés de : " + points);
                joueur.setPointsProduction(joueur.getPointsProduction() + points); // Mettre à jour l'objet local
            } else {
                System.out.println("Aucune mise à jour effectuée. Vérifiez l'ID du joueur.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de points de production.", e);
        }
        System.out.println("********* Nouvelle valeur des points : " + joueur.getPointsProduction());
    }


    public void endGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = Game.getInstance();

        if (game == null || game.getJoueurs().isEmpty()) {
            response.sendRedirect("errorPage"); // Redirige si la partie n'est pas valide
            return;
        }

        try (Connection connection = DBConnection.getConnection()) {
            // Requête pour récupérer les scores des joueurs depuis la base de données
            String query = "SELECT id, score FROM joueurs WHERE id IN (" +
                    game.getJoueurs().stream().map(String::valueOf).collect(Collectors.joining(",")) +
                    ") ORDER BY score DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            List<Joueur> joueurs = new ArrayList<>();
            while (rs.next()) {
                int joueurId = rs.getInt("id");
                int score = rs.getInt("score");
                Joueur joueur = Joueur.getJoueurById(joueurId);
                joueur.setScore(score); // Met à jour le score dans l'objet Joueur
                joueurs.add(joueur);
            }

            if (joueurs.isEmpty()) {
                response.sendRedirect("errorPage");
                return;
            }

            // Trier les joueurs par score décroissant
            joueurs.sort(Comparator.comparingInt(Joueur::getScore).reversed());

            // Identifiez le gagnant et les perdants
            Joueur gagnant = joueurs.get(0);
            List<Joueur> perdants = joueurs.subList(1, joueurs.size());

            // Définir les attributs de session pour chaque joueur connecté
            for (Integer joueurId : game.getJoueurs()) {
                Joueur joueur = Joueur.getJoueurById(joueurId);
                HttpSession session = request.getSession(false); // Récupère la session existante
                if (session != null) {
                    if (joueur.equals(gagnant)) {
                        session.setAttribute("flashSuccess", "Félicitations ! Vous avez gagné la partie avec un score de " + gagnant.getScore() + " !");
                    } else {
                        session.setAttribute("flashErreur", "Vous avez perdu. Votre score est de " + joueur.getScore() + ".");
                    }
                }
            }

            // Redirigez tous les joueurs connectés vers la page de score
            //response.sendRedirect("score");
            request.getRequestDispatcher("Views/endGame.jsp").forward(request, response);
        } catch (SQLException | ServletException e) {
            e.printStackTrace();
            response.sendRedirect("errorPage"); // Gère les erreurs SQL
        }
    }


}