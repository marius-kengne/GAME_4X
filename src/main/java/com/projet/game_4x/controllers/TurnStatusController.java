package com.projet.game_4x.controllers;

import com.projet.game_4x.models.Game;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "TurnStatusController", value = "/turnStatus")
public class TurnStatusController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException {

        Game game = Game.getInstance();

        int currentPlayer = game.getCurrentPlayer();

        System.out.println("##### prochain joueur " + game.getCurrentPlayer());
        System.out.println("***liste des joeueurs " + game.getJoueurs().toString());
        // Envoyer une réponse JSON
        response.setContentType("application/json");
        response.getWriter().write("{\"currentPlayer\": " + currentPlayer + "}");
    }

}
