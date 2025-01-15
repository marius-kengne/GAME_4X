package com.projet.game_4x.controllers;

import com.projet.game_4x.models.*;
import com.projet.game_4x.utils.DBConnection;
import com.projet.game_4x.utils.GameWebSocket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

@WebServlet(name = "DeplacementController", value = "/deplacerSoldat")
public class DeplacementController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Game game = Game.getInstance();
        HttpSession session = request.getSession();
        Joueur joueur = (Joueur) session.getAttribute("joueur");
        //Carte carte = (Carte) getServletContext().getAttribute("carte");
        Carte carte = (Carte) game.getCarte();

        // Vérifier si c'est le tour du joueur
        if (game.getCurrentPlayer() != joueur.getId()) {
            request.setAttribute("erreur", "Ce n'est pas votre tour !");
            request.getRequestDispatcher("Views/start.jsp").forward(request, response);
            return;
        }

        if (joueur == null || carte == null) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("direction");
        String soldatIdParam = request.getParameter("soldatId");

        if (action == null || soldatIdParam == null || soldatIdParam.isEmpty()) {
            request.setAttribute("erreur", "Action ou soldat non spécifié.");
            request.getRequestDispatcher("Views/game.jsp").forward(request, response);
            return;
        }

        int soldatId = Integer.parseInt(soldatIdParam);
        Soldat soldat = joueur.getSoldats().stream()
                .filter(s -> s.getId() == soldatId)
                .findFirst()
                .orElse(null);

        if (soldat == null) {
            request.setAttribute("erreur", "Soldat introuvable ou non autorisé.");
            request.getRequestDispatcher("Views/game.jsp").forward(request, response);
            return;
        }

        Tuile currentPosition = soldat.getPosition();
        //Tuile destination = null;

        // Calculer la destination
        Tuile destination = calculateDestination(action, carte, currentPosition);

        // Validation et logique de déplacement
        if (destination == null) {
            String erreur = "Déplacement impossible. La destination est hors de la carte.";
            request.setAttribute("erreur", erreur);
            //GameWebSocket.sendToClient(joueur.getLogin(), erreur);
            GameWebSocket.broadcast("Le soldat a été déplacé vers la position X=");
            request.getSession().setAttribute("flashErreur", "Déplacement impossible. La destination est hors de la carte.");
        } else if ("montagne".equals(destination.getType())) {
            String erreur = "Déplacement bloqué : La tuile est une montagne.";
            request.setAttribute("erreur", erreur);
            //GameWebSocket.sendToClient(joueur.getLogin(), erreur);
            GameWebSocket.broadcast("Le soldat a été déplacé vers la position X=");
            request.getSession().setAttribute("flashErreur", "Déplacement bloqué : La tuile est une montagne.");
        } else if ("ville".equals(destination.getType()) && destination.getProprietaire() == null) {
            //gestion des villes
            System.out.println("************** gestion ville");
            //int degats = Math.max(soldat.getPointsDAttaque() - destination.getPointsDeDefense(), 0); // Le soldat attaque avec des points aléatoires
            Random random = new Random();
            int minDegats = Math.min(soldat.getPointsDAttaque(), destination.getPointsDeDefense());
            int maxDegats = Math.max(soldat.getPointsDAttaque(), destination.getPointsDeDefense());
            int degats = random.nextInt(maxDegats - minDegats + 1) + minDegats;
            System.out.println("************** degats " +degats);

            destination.setPointsDeDefense(destination.getPointsDeDefense() - degats);

            if (destination.getPointsDeDefense() <= 0) {
                System.out.println("************** Ville capturée");
                // Ville capturée
                destination.setProprietaire(joueur); // Le joueur devient propriétaire
                destination.setPointsDeDefense(0); // Défense réduite à zéro

                try (Connection connection = DBConnection.getConnection()) {
                    connection.setAutoCommit(false);

                    updateTuileProprietaire(connection, destination); // Mise à jour du propriétaire
                    updateSoldatPosition(connection, soldat, destination); // Déplacer le soldat sur la ville
                    updateTuileToVide(connection, currentPosition); // Ancienne position vide

                    connection.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Erreur lors de la capture de la ville.", e);
                }

                // Message pour l'utilisateur
                request.setAttribute("message", "Ville capturée !");
                request.getSession().setAttribute("flashSuccess", "Ville capturée !");
                GameWebSocket.broadcast("La ville en X=" + destination.getX() + ", Y=" + destination.getY() + " a été capturée par le joueur " + joueur.getLogin());
            } else {
                System.out.println("************** ID Ville : " + destination.getId());
                System.out.println("************** Ville non capturée");
                // Ville non capturée
                try (Connection connection = DBConnection.getConnection()) {
                    connection.setAutoCommit(false);

                    updateTuile(connection, destination); // Mise à jour des points de défense de la ville

                    connection.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Erreur lors de la mise à jour de la ville après l'attaque.", e);
                }

                GameWebSocket.broadcast("Le soldat " + soldat.getId() + " a éliminé un ennemi en X=" + destination.getX() + ", Y=" + destination.getY());
                //request.setAttribute("erreur", "Ville attaquée mais pas encore capturée !");
                request.getSession().setAttribute("flashErreur", "Ville attaquée mais pas encore capturée !");
            }

        } else if (destination.getSoldat() != null && destination.getSoldat().getProprietaire().getId() != joueur.getId()) {
            /*
            Soldat ennemi = destination.getSoldat();
            int degats = soldat.attaquer(ennemi);
            if (ennemi.getPointsDeVie() <= 0) {
                destination.setSoldat(null);
                soldat.setPosition(destination);
                destination.setSoldat(soldat);
                updateSoldatPosition(soldat, destination);
                GameWebSocket.broadcast("Le soldat a été déplacé vers la position X=");
                request.setAttribute("message", "Soldat ennemi neutralisé.");
            } else {
                GameWebSocket.broadcast("Le soldat a été déplacé vers la position X=");
                request.setAttribute("erreur", "Combat en cours, l'ennemi a survécu.");
            }
             */
            Soldat ennemi = destination.getSoldat();
            int degats = soldat.attaquer(ennemi); // Le soldat attaque avec des points aléatoires
            System.out.println("********** start combat");
            System.out.println("********** degats : " +degats);
            System.out.println("********** point de vie ennemi : " +ennemi.getPointsDeVie());
            System.out.println("********** point de defense ennemi : " +ennemi.getPointsDeDefense());
            if (ennemi.getPointsDeVie() <= 0 || ennemi.getPointsDeDefense() <=0) {
                // Ennemi éliminé
                destination.setSoldat(null); // Retirer le soldat ennemi
                soldat.setPosition(destination); // Déplacer le soldat sur la tuile ennemie
                destination.setSoldat(soldat);
                try (Connection connection = DBConnection.getConnection()) {
                    connection.setAutoCommit(false);
                    // Mise à jour en base de données
                    supprimerSoldat(connection, ennemi); // Supprime l'ennemi de la base
                    updateSoldatPosition(connection, soldat, destination); // Met à jour la position du soldat
                    updateTuileToVide(connection, currentPosition); // Met l'ancienne position comme vide
                    connection.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Erreur lors de l'attaque du soldat.", e);
                }

                carte = Carte.chargerCarteExistante(carte.getId());
                game.setCarte(carte);

                // Message pour l'utilisateur
                request.setAttribute("message", "Soldat ennemi neutralisé !");
                request.getSession().setAttribute("flashSuccess", "Soldat ennemi neutralisé !");
                GameWebSocket.broadcast("Le soldat " + soldat.getId() + " a éliminé un ennemi en X=" + destination.getX() + ", Y=" + destination.getY());
            } else {
                // L'ennemi survit
                try (Connection connection = DBConnection.getConnection()) {
                    connection.setAutoCommit(false);
                    updateSoldat(connection, ennemi); // Mise à jour des points de vie de l'ennemi
                    connection.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Erreur lors de la mise à jour après l'attaque.", e);
                }

                carte = Carte.chargerCarteExistante(carte.getId());
                game.setCarte(carte);

                GameWebSocket.broadcast("Le soldat " + soldat.getId() + " a éliminé un ennemi en X=" + destination.getX() + ", Y=" + destination.getY());
                request.setAttribute("erreur", "Combat en cours. L'ennemi a survécu !");
                request.getSession().setAttribute("flashErreur", "Combat en cours. L'ennemi a survécu !");
            }
        } else {
            // Déplacement vers une tuile vide
            try (Connection connection = DBConnection.getConnection()) {
                connection.setAutoCommit(false); // Activer une transaction pour garantir la cohérence

                // Mettre à jour la tuile actuelle comme vide dans la BD
                updateTuileToVide(connection, currentPosition);

                // Mettre à jour le soldat pour qu'il occupe la nouvelle tuile dans la BD
                updateSoldatPosition(connection, soldat, destination);

                // Mettre à jour la carte dans les objets en mémoire
                currentPosition.setSoldat(null); // Retirer le soldat de l'ancienne position
                currentPosition.setType("vide"); // Définir la tuile actuelle comme vide
                destination.setSoldat(soldat); // Associer le soldat à la nouvelle tuile
                soldat.setPosition(destination); // Mettre à jour la position du soldat

                connection.commit(); // Confirmer les modifications dans la base de données
                request.setAttribute("message", "Déplacement effectué avec succès.");
                request.getSession().setAttribute("flashSuccess", "Déplacement effectué avec succès !");

                final Tuile dest = destination;
                // Mettez à jour les tuiles et soldats sur la carte
                carte.getTuiles().forEach(tuile -> {
                    if (tuile.getId() == currentPosition.getId()) {
                        tuile.setSoldat(null);
                        tuile.setType("vide");
                    } else if (tuile.getId() == dest.getId()) {
                        tuile.setSoldat(soldat);
                    }
                });
                // Remettre la carte mise à jour dans le contexte
                game.setCarte(carte);
                //getServletContext().setAttribute("carte", carte);

                // Exemple dans DeplacementController
                GameWebSocket.broadcast("Le soldat a été déplacé vers la position X=" + destination.getX() + ", Y=" + destination.getY());

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Erreur lors du déplacement du soldat.", e);
            }
        }

        /*
        if (destination != null){
            final Tuile dest = destination;
            // Mettez à jour les tuiles et soldats sur la carte
            carte.getTuiles().forEach(tuile -> {
                if (tuile.getId() == currentPosition.getId()) {
                    tuile.setSoldat(null);
                    tuile.setType("vide");
                } else if (tuile.getId() == dest.getId()) {
                    tuile.setSoldat(soldat);
                }
            });
            // Remettre la carte mise à jour dans le contexte
            getServletContext().setAttribute("carte", carte);
        }*/
        //game.nextPlayer();
        joueur = Joueur.getJoueurById(joueur.getId());
        try {
            joueur = Joueur.chargerSoldatJoueur(joueur);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        session.setAttribute("joueur", joueur);
        Object tourActuel = session.getAttribute("tourActuel");
        if (tourActuel == null){
            session.setAttribute("tourActuel", 0);
        }else {
            int tour = (int) session.getAttribute("tourActuel");
            session.setAttribute("tourActuel", tour+1);
        }
        //request.setAttribute("carte", carte);
        //request.setAttribute("tourActuel", getServletContext().getAttribute("tourActuel"));
        //request.getRequestDispatcher("Views/game.jsp").forward(request, response);
        //response.sendRedirect("home");
        //request.getRequestDispatcher("Views/start.jsp").forward(request, response);
        response.sendRedirect("game");
    }

    private Tuile calculateDestination(String action, Carte carte, Tuile currentPosition) {
        switch (action) {
            case "moveNorth":
                return currentPosition.getY() > 0 ? carte.getTuile(currentPosition.getX(), currentPosition.getY() - 1) : null;
            case "moveSouth":
                return currentPosition.getY() < carte.getHauteur() - 1 ? carte.getTuile(currentPosition.getX(), currentPosition.getY() + 1) : null;
            case "moveEast":
                return currentPosition.getX() < carte.getLargeur() - 1 ? carte.getTuile(currentPosition.getX() + 1, currentPosition.getY()) : null;
            case "moveWest":
                return currentPosition.getX() > 0 ? carte.getTuile(currentPosition.getX() - 1, currentPosition.getY()) : null;
            default:
                return null;
        }
    }

    private void processMovement(Joueur joueur, Carte carte, Soldat soldat, Tuile currentPosition, Tuile destination) {
        synchronized (carte) {
            if ("montagne".equals(destination.getType())) {
                throw new IllegalArgumentException("Déplacement bloqué : tuile montagne.");
            }

            currentPosition.setSoldat(null);
            currentPosition.setType("vide");
            destination.setSoldat(soldat);
            soldat.setPosition(destination);
        }
    }

    private void updateTuileToVide(Connection connection, Tuile tuile) throws SQLException {
        String query = "UPDATE tuiles SET type = 'vide', proprietaire_id = NULL WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, tuile.getId());
            stmt.executeUpdate();
        }
    }

    //pour l'attaque de soldat
    private void updateSoldatPosition(Soldat soldat, Tuile nouvellePosition) {
        if (nouvellePosition == null) {
            throw new IllegalArgumentException("Un soldat doit toujours avoir une position valide.");
        }

        try (Connection connection = DBConnection.getConnection()) {
            String query = "UPDATE soldats SET position_tuile_id = ? WHERE id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, nouvellePosition.getId());
            stmt.setInt(2, soldat.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour de la position du soldat.", e);
        }
    }

    private void updateSoldatPosition(Connection connection, Soldat soldat, Tuile nouvellePosition) throws SQLException {
        if (nouvellePosition == null) {
            throw new IllegalArgumentException("Un soldat doit toujours avoir une position valide.");
        }

        String query = "UPDATE soldats SET position_tuile_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nouvellePosition.getId());
            stmt.setInt(2, soldat.getId());
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

    private void supprimerSoldat(Connection connection, Soldat soldat) throws SQLException {
        String query = "DELETE FROM soldats WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, soldat.getId());
            stmt.executeUpdate();
        }
    }

    private void updateSoldat(Connection connection, Soldat soldat) throws SQLException {
        String query = "UPDATE soldats SET points_de_vie = ?, points_de_defense = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, soldat.getPointsDeVie());
            stmt.setInt(2, soldat.getPointsDeDefense());
            stmt.setInt(3, soldat.getId());
            stmt.executeUpdate();
        }
    }

    private void updateTuile(Connection connection, Tuile tuile) throws SQLException {
        String query = "UPDATE tuiles SET points_de_defense = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, tuile.getPointsDeDefense());
            stmt.setInt(2, tuile.getId());
            stmt.executeUpdate();
        }
    }

    private void updateTuileProprietaire(Connection connection, Tuile tuile) throws SQLException {
        String query = "UPDATE tuiles SET proprietaire_id = ?, points_de_defense = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, tuile.getProprietaire().getId());
            stmt.setInt(2, 0); // Défense mise à zéro après capture
            stmt.setInt(3, tuile.getId());
            stmt.executeUpdate();
        }
    }
}