package com.projet.game_4x.controllers;

import com.projet.game_4x.models.Carte;
import com.projet.game_4x.models.Joueur;
import com.projet.game_4x.models.Soldat;
import com.projet.game_4x.models.Tuile;
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

@WebServlet(name = "DeplacementController", value = "/deplacerSoldat")
public class DeplacementController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Joueur joueur = (Joueur) session.getAttribute("joueur");
        Carte carte = (Carte) getServletContext().getAttribute("carte");

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
        } else if ("montagne".equals(destination.getType())) {
            String erreur = "Déplacement bloqué : La tuile est une montagne.";
            request.setAttribute("erreur", erreur);
            //GameWebSocket.sendToClient(joueur.getLogin(), erreur);
        } else if ("ville".equals(destination.getType()) && destination.getProprietaire() == null) {
            if (destination.getPointsDeDefense() > 0) {
                destination.setPointsDeDefense(destination.getPointsDeDefense() - soldat.getPointsDAttaque());
                if (destination.getPointsDeDefense() <= 0) {
                    destination.setProprietaire(joueur);
                    updateTuileProprietaire(destination);
                    request.setAttribute("message", "Ville conquise !");
                } else {
                    request.setAttribute("erreur", "Ville attaquée, mais pas encore conquise.");
                }
            }
        } else if (destination.getSoldat() != null && destination.getSoldat().getProprietaire().getId() != joueur.getId()) {
            Soldat ennemi = destination.getSoldat();
            int degats = soldat.attaquer(ennemi);
            if (ennemi.getPointsDeVie() <= 0) {
                destination.setSoldat(null);
                soldat.setPosition(destination);
                destination.setSoldat(soldat);
                //updateSoldatPosition(soldat, destination);
                request.setAttribute("message", "Soldat ennemi neutralisé.");
            } else {
                request.setAttribute("erreur", "Combat en cours, l'ennemi a survécu.");
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


        //request.setAttribute("carte", carte);
        request.setAttribute("tourActuel", getServletContext().getAttribute("tourActuel"));
        request.getRequestDispatcher("Views/game.jsp").forward(request, response);
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

    /*
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
    */
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
}