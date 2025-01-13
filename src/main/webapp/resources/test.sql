CREATE TABLE Joueur (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        login VARCHAR(50) NOT NULL UNIQUE,
                        motDePasse VARCHAR(100) NOT NULL,
                        pointsProduction INT DEFAULT 0,
                        score INT DEFAULT 0
);
CREATE TABLE Tuile (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       x INT NOT NULL,
                       y INT NOT NULL,
                       pointsDeDefense INT DEFAULT 0,
                       joueur_id INT NULL,
                       FOREIGN KEY (joueur_id) REFERENCES Joueur(id)
);
CREATE TABLE Ville (
                       tuile_id INT PRIMARY KEY,
                       pointsDeProduction INT DEFAULT 0,
                       pointsDeProductionParTour INT DEFAULT 0,
                       FOREIGN KEY (tuile_id) REFERENCES Tuile(id)
);
CREATE TABLE Foret (
                       tuile_id INT PRIMARY KEY,
                       quantiteRessources INT DEFAULT 0,
                       FOREIGN KEY (tuile_id) REFERENCES Tuile(id)
);
CREATE TABLE Montagne (
                          tuile_id INT PRIMARY KEY,
                          FOREIGN KEY (tuile_id) REFERENCES Tuile(id)
);
CREATE TABLE Soldat (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        pointsDAttaque INT DEFAULT 0,
                        pointsDeDefense INT DEFAULT 0,
                        pointsDeVie INT DEFAULT 100,
                        position_tuile_id INT,
                        joueur_id INT,
                        FOREIGN KEY (position_tuile_id) REFERENCES Tuile(id),
                        FOREIGN KEY (joueur_id) REFERENCES Joueur(id)
);
CREATE TABLE Carte (
                       id INT PRIMARY KEY AUTO_INCREMENT,
                       hauteur INT NOT NULL,
                       largeur INT NOT NULL
);
CREATE TABLE Carte_Tuiles (
                              carte_id INT,
                              tuile_id INT,
                              PRIMARY KEY (carte_id, tuile_id),
                              FOREIGN KEY (carte_id) REFERENCES Carte(id),
                              FOREIGN KEY (tuile_id) REFERENCES Tuile(id)
);
