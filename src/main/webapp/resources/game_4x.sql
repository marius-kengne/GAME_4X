CREATE DATABASE IF NOT EXISTS game_4x;
USE game_4x;
CREATE TABLE cartes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    largeur INT NOT NULL,
    hauteur INT NOT NULL
);

CREATE TABLE tuiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    carte_id INT NOT NULL,
    type VARCHAR(20) NOT NULL,
    x INT NOT NULL,
    y INT NOT NULL,
    proprietaire_id INT DEFAULT NULL,
    points_de_defense INT DEFAULT 0,
    FOREIGN KEY (carte_id) REFERENCES cartes(id),
    FOREIGN KEY (proprietaire_id) REFERENCES joueurs(id)
);

CREATE TABLE joueurs (
     id INT AUTO_INCREMENT PRIMARY KEY,
     login VARCHAR(50) UNIQUE NOT NULL,
     mot_de_passe VARCHAR(255) NOT NULL,
     score INT DEFAULT 0,
     points_de_production INT DEFAULT 0
);

CREATE TABLE villes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    proprietaire_id INT DEFAULT NULL,
    points_de_defense INT DEFAULT 10,
    points_de_production INT DEFAULT 5,
    FOREIGN KEY (proprietaire_id) REFERENCES joueurs(id)
);

CREATE TABLE soldats (
     id INT AUTO_INCREMENT PRIMARY KEY,
     proprietaire_id INT NOT NULL,
     position_tuile_id INT NOT NULL,
     points_de_vie INT DEFAULT 100,
     points_d_attaque INT DEFAULT 10,
     points_de_defense INT DEFAULT 5,
     FOREIGN KEY (proprietaire_id) REFERENCES joueurs(id),
     FOREIGN KEY (position_tuile_id) REFERENCES tuiles(id)
);

CREATE TABLE forets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    quantite_ressources INT DEFAULT 50
);

CREATE TABLE montagnes (
   id INT AUTO_INCREMENT PRIMARY KEY,
   x INT NOT NULL,
   y INT NOT NULL
);