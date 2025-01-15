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
import java.sql.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.projet.game_4x.models.Game.ajouterPointsDeProductionAuJoueur;

@WebServlet(name = "ActionsController", value = "/actions")
public class ActionsController extends HttpServlet {
    private static final int SOLDAT_COST = 15;
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Récupérer l'action depuis le formulaire
        String action = request.getParameter("direction");
        String message;

        HttpSession session = request.getSession();
        Object tourActuel = session.getAttribute("tourActuel");

        Game game = Game.getInstance();
        Carte carte = game.getCarte();
        Joueur joueur = (Joueur) session.getAttribute("joueur");

        if (joueur == null || carte == null) {
            response.sendRedirect("login");
            return;
        }

        // Gérer les actions
        switch (action) {
            case "heal":
                ajouterPointsDeProductionAuJoueur(joueur,15);
                joueur = Joueur.getJoueurById(joueur.getId());
                try {
                    joueur = Joueur.chargerSoldatJoueur(joueur);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                session.setAttribute("joueur",joueur);
                if (tourActuel == null){
                    session.setAttribute("tourActuel", 0);
                }else {
                    int tour = (int) session.getAttribute("tourActuel");
                    session.setAttribute("tourActuel", tour+1);
                }
                String msg = "Le soldat du joueuer " + joueur.getLogin() + " a bien été soigné";
                GameWebSocket.broadcast(msg);
                request.getSession().setAttribute("flashSuccess", msg);
                break;
            case "forage":
                ajouterPointsDeProductionAuJoueur(joueur,15);
                joueur = Joueur.getJoueurById(joueur.getId());
                try {
                    joueur = Joueur.chargerSoldatJoueur(joueur);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                session.setAttribute("joueur",joueur);
                if (tourActuel == null){
                    session.setAttribute("tourActuel", 0);
                }else {
                    int tour = (int) session.getAttribute("tourActuel");
                    session.setAttribute("tourActuel", tour+1);
                }
                msg = "Le soldat du joueuer " + joueur.getLogin() + " a trouvé des ressources";
                GameWebSocket.broadcast(msg);
                request.getSession().setAttribute("flashSuccess", msg);
                break;
            case "endGame":
                //terminerPartie(request, response);
                response.sendRedirect("endGame");
                return;
            case "endTurn":
                // Passer au joueur suivant
                //Game game = Game.getInstance();
                game.nextPlayer();
                joueur = Joueur.getJoueurById(joueur.getId());
                try {
                    joueur = Joueur.chargerSoldatJoueur(joueur);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                session.setAttribute("joueur",joueur);
                if (tourActuel == null){
                    session.setAttribute("tourActuel", 0);
                }else {
                    int tour = (int) session.getAttribute("tourActuel");
                    session.setAttribute("tourActuel", tour+1);
                }
                String txt = "Fin du tour de " + joueur.getLogin();
                GameWebSocket.broadcast(txt);
                request.getSession().setAttribute("flashSuccess", txt);
                break;

            case "recruit":
                // gestion du recrutement du soldat
                // Vérifier si le joueur a suffisamment de points de production
                if (joueur.getPointsProduction() < SOLDAT_COST) {
                    //request.setAttribute("erreur", "Pas assez de points de production pour recruter un soldat !");
                    request.getSession().setAttribute("flashErreur", "Pas assez de points de production pour recruter un soldat !");
                    response.sendRedirect("game");
                    return;
                }

                // Trouver une tuile vide sans propriétaire pour placer le soldat
                Tuile tuileVide = null;
                try {
                    tuileVide = trouverTuileVideSansProprietaire(DBConnection.getConnection(), carte.getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                if (tuileVide == null) {
                    throw new RuntimeException("Aucune tuile vide disponible pour recruter un soldat !");
                }

                try (Connection connection = DBConnection.getConnection()) {
                    connection.setAutoCommit(false);

                    // Réduire les points de production du joueur
                    joueur.setPointsProduction(joueur.getPointsProduction() - SOLDAT_COST);
                    updateJoueurPointsProduction(connection, joueur);

                    // Créer et insérer le soldat dans la base de données
                    Soldat nouveauSoldat = new Soldat(0, joueur, tuileVide, 100, 10, 5);
                    int soldatId = insertSoldat(connection, nouveauSoldat);

                    // Mettre à jour la tuile pour y associer le nouveau soldat
                    nouveauSoldat.setId(soldatId);
                    tuileVide.setSoldat(nouveauSoldat);
                    updateTuile(connection, tuileVide);

                    // Mettre à jour la carte dans l'objet Game
                    joueur.ajouterSoldat(nouveauSoldat);
                    carte = Carte.chargerCarteExistante(carte.getId());
                    game.setCarte(carte);

                    connection.commit();

                    GameWebSocket.broadcast("Le soldat a été déplacé vers la position X=");
                    request.getSession().setAttribute("flashSuccess", "Soldat recruté avec succès !");

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Erreur lors du recrutement du soldat.", e);
                }

                break;
            default:
                message = "Action non reconnue !";
        }

        response.sendRedirect("game");
    }

    private String deplacerSoldat(HttpServletRequest request, Joueur joueur, int dx, int dy, Carte carte) {
        int x = Integer.parseInt(request.getParameter("x"));
        int y = Integer.parseInt(request.getParameter("y"));

        Tuile origine = carte.getTuile(x, y);
        Tuile destination = carte.getTuile(x + dx, y + dy);

        if (origine == null || origine.getSoldat() == null || !origine.getSoldat().getProprietaire().equals(joueur)) {
            return "Aucun soldat à déplacer ou déplacement non autorisé.";
        }

        if (destination == null || destination.getType().equals("montagne")) {
            return "Déplacement impossible (montagne ou hors carte).";
        }

        // Déplacer le soldat
        Soldat soldat = origine.getSoldat();
        origine.setSoldat(null);
        destination.setSoldat(soldat);
        soldat.setPosition(destination);

        return "Soldat déplacé.";
    }

    private String soignerSoldat(HttpServletRequest request, Joueur joueur) {
        int x = Integer.parseInt(request.getParameter("x"));
        int y = Integer.parseInt(request.getParameter("y"));
        Carte carte = (Carte) getServletContext().getAttribute("carte");

        Tuile tuile = carte.getTuile(x, y);
        if (tuile != null && tuile.getSoldat() != null && tuile.getSoldat().getProprietaire().equals(joueur)) {
            Soldat soldat = tuile.getSoldat();
            soldat.soigner(5); // Soigne 5 points
            return "Le soldat a été soigné.";
        }
        return "Aucun soldat à soigner.";
    }

    private String forager(HttpServletRequest request, Joueur joueur) {
        int x = Integer.parseInt(request.getParameter("x"));
        int y = Integer.parseInt(request.getParameter("y"));
        Carte carte = (Carte) getServletContext().getAttribute("carte");

        Tuile tuile = carte.getTuile(x, y);
        if (tuile != null && tuile.getType().equals("foret") && tuile.getSoldat() != null && tuile.getSoldat().getProprietaire().equals(joueur)) {
            joueur.setPointsProduction(joueur.getPointsProduction() + 5);
            return "Points de production collectés.";
        }
        return "Aucune ressource à collecter.";
    }

    private void updateJoueurPointsProduction(Connection connection, Joueur joueur) throws SQLException {
        String query = "UPDATE joueurs SET points_de_production = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, joueur.getPointsProduction());
            stmt.setInt(2, joueur.getId());
            stmt.executeUpdate();
        }
    }

    private int insertSoldat(Connection connection, Soldat soldat) throws SQLException {
        String query = "INSERT INTO soldats (proprietaire_id, position_tuile_id, points_de_vie, points_d_attaque, points_de_defense) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, soldat.getProprietaire().getId());
            stmt.setInt(2, soldat.getPosition().getId());
            stmt.setInt(3, soldat.getPointsDeVie());
            stmt.setInt(4, soldat.getPointsDAttaque());
            stmt.setInt(5, soldat.getPointsDeDefense());
            stmt.executeUpdate();

            // Récupérer l'ID généré pour le soldat
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Échec de la création du soldat, aucun ID généré.");
                }
            }
        }
    }

    private void updateTuile(Connection connection, Tuile tuile) throws SQLException {
        String query = "UPDATE tuiles SET proprietaire_id = ?, points_de_defense = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setObject(1, tuile.getProprietaire() != null ? tuile.getProprietaire().getId() : null, java.sql.Types.INTEGER);
            stmt.setInt(2, tuile.getPointsDeDefense());
            stmt.setString(3, tuile.getType());
            stmt.setInt(4, tuile.getId());
            stmt.executeUpdate();
        }
    }

    // Trouver une tuile vide sans propriétaire pour placer le soldat
    private Tuile trouverTuileVideSansProprietaire(Connection connection, int carteId) throws SQLException {
        String query = """
        SELECT t.id, t.type, t.x, t.y, t.points_de_defense, t.proprietaire_id
        FROM tuiles t
        WHERE t.type = 'vide' AND t.proprietaire_id IS NULL AND 
              t.id NOT IN (SELECT position_tuile_id FROM soldats)
              AND t.carte_id = ?
        LIMIT 1
    """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, carteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Tuile(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        null, // Propriétaire null
                        rs.getInt("points_de_defense")
                );
            } else {
                throw new RuntimeException("Aucune tuile vide disponible pour placer un soldat !");
            }
        }
    }


    public static void terminerPartie(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Game game = Game.getInstance();

        if (game == null || game.getJoueurs().isEmpty()) {
            response.sendRedirect("errorPage"); // Redirige si la partie n'est pas valide
            return;
        }

        // Obtenez les IDs des joueurs
        List<Integer> joueurIds = game.getJoueurs();

        // Mappez les IDs vers les objets Joueur
        List<Joueur> joueurs = joueurIds.stream()
                .map(id -> Joueur.getJoueurById(id)) // Utilise correctement `getJoueurById`
                .collect(Collectors.toList());

        // Trier les joueurs par score décroissant
        joueurs.sort(Comparator.comparingInt(Joueur::getScore).reversed());

        // Identifiez le gagnant et les perdants
        Joueur gagnant = joueurs.get(0);
        List<Joueur> perdants = joueurs.subList(1, joueurs.size());

        // Définir les attributs de session pour chaque joueur
        for (Integer joueurId : joueurIds) {
            Joueur joueur = Joueur.getJoueurById(joueurId);
            HttpSession session = request.getSession(false); // Récupérer la session existante pour ce joueur
            if (session != null) {
                if (joueur.equals(gagnant)) {
                    session.setAttribute("flashSuccess", "Félicitations ! Vous avez gagné la partie avec un score de " + gagnant.getScore() + " !");
                } else {
                    session.setAttribute("flashErreur", "Vous avez perdu. Votre score est de " + joueur.getScore() + ".");
                }
            }
        }

        // Redirigez tous les utilisateurs vers la page de score
        //response.sendRedirect("endGame");
        request.getRequestDispatcher("Views/endGame.jsp").forward(request, response);
    }


}
