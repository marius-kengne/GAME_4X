package com.projet.game_4x.controllers;

import com.projet.game_4x.models.Joueur;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
@WebServlet(name = "HomeController", value = "/home")
public class HomeController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, IOException {

        HttpSession session = request.getSession();
        Joueur joueur = (Joueur) session.getAttribute("joueur");

        if (joueur == null) {
            response.sendRedirect("login");
            return;
        }
        request.getRequestDispatcher("Views/home.jsp").forward(request, response);
    }

}
