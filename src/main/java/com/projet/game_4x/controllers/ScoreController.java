package com.projet.game_4x.controllers;

import com.projet.game_4x.models.Joueur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "ScoreController", value = "/score")
public class ScoreController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Joueur joueur = (Joueur) session.getAttribute("joueur");

        if (joueur != null) {
            // Calculer et sauvegarder le score
            int score = joueur.calculerScore();
            joueur.sauvegarderScore();

            // Ajouter le score à la requête
            request.setAttribute("score", score);
            try {
                request.setAttribute("territoires", joueur.nombreTerritoire());
                request.setAttribute("nombreSoldat", joueur.nombreSoldat());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            request.setAttribute("erreur", "Joueur introuvable. Veuillez vous reconnecter.");
        }

        // Rediriger vers la vue
        request.getRequestDispatcher("/Views/score.jsp").forward(request, response);
    }

}
