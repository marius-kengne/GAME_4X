CREATE DATABASE IF NOT EXISTS game_4x;
USE game_4x;

-- Table des cartes
CREATE TABLE cartes (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        largeur INT NOT NULL,
                        hauteur INT NOT NULL
);

-- Table des joueurs
CREATE TABLE joueurs (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         login VARCHAR(50) UNIQUE NOT NULL,
                         mot_de_passe VARCHAR(255) NOT NULL,
                         score INT DEFAULT 0,
                         points_de_production INT DEFAULT 0
);

-- Table des tuiles
CREATE TABLE tuiles (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        carte_id INT NOT NULL,
                        type VARCHAR(20) NOT NULL, -- Peut être 'ville', 'montagne', 'foret', ou 'vide'
                        x INT NOT NULL,
                        y INT NOT NULL,
                        proprietaire_id INT DEFAULT NULL,
                        points_de_defense INT DEFAULT 0,
                        FOREIGN KEY (carte_id) REFERENCES cartes(id) ON DELETE CASCADE,
                        FOREIGN KEY (proprietaire_id) REFERENCES joueurs(id) ON DELETE SET NULL
);

-- Table des villes
CREATE TABLE villes (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        tuile_id INT NOT NULL,
                        proprietaire_id INT DEFAULT NULL,
                        points_de_defense INT DEFAULT 10,
                        points_de_production INT DEFAULT 5,
                        FOREIGN KEY (tuile_id) REFERENCES tuiles(id) ON DELETE CASCADE,
                        FOREIGN KEY (proprietaire_id) REFERENCES joueurs(id) ON DELETE SET NULL
);

-- Table des soldats
CREATE TABLE soldats (
                         id INT AUTO_INCREMENT PRIMARY KEY,
                         proprietaire_id INT NOT NULL,
                         position_tuile_id INT NOT NULL,
                         points_de_vie INT DEFAULT 100,
                         points_d_attaque INT DEFAULT 10,
                         points_de_defense INT DEFAULT 5,
                         FOREIGN KEY (proprietaire_id) REFERENCES joueurs(id) ON DELETE CASCADE,
                         FOREIGN KEY (position_tuile_id) REFERENCES tuiles(id) ON DELETE CASCADE
);

-- Table des forêts
CREATE TABLE forets (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        tuile_id INT NOT NULL,
                        quantite_ressources INT DEFAULT 50,
                        FOREIGN KEY (tuile_id) REFERENCES tuiles(id) ON DELETE CASCADE
);

-- Table des montagnes
CREATE TABLE montagnes (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           tuile_id INT NOT NULL,
                           FOREIGN KEY (tuile_id) REFERENCES tuiles(id) ON DELETE CASCADE
);

-- Indices pour optimiser les recherches sur les coordonnées des tuiles
CREATE UNIQUE INDEX idx_tuile_coords ON tuiles (carte_id, x, y);
