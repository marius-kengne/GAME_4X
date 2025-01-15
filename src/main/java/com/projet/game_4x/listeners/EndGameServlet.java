package com.projet.game_4x.listeners;

import com.projet.game_4x.models.Game;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/endGame")
public class EndGameServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Game game = Game.getInstance();
        game.endGame(request, response); // Appelle la méthode pour terminer la partie
    }
}
