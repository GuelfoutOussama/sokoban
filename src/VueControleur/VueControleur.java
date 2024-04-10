package VueControleur;

import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.PrimitiveIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.Graphics;

import javax.sound.sampled.*;

import modele.*;
import java.awt.BorderLayout;


/** Cette classe a deux fonctions :
 *  (1) Vue : proposer une représentation graphique de l'application (cases graphiques, etc.)
 *  (2) Controleur : écouter les évènements clavier et déclencher le traitement adapté sur le modèle (flèches direction Pacman, etc.))
 *
 */
public class VueControleur extends JFrame implements Observer {
    private Jeu jeu; // référence sur une classe de modèle : permet d'accéder aux données du modèle pour le rafraichissement, permet de communiquer les actions clavier (ou souris)

    private int sizeX; // taille de la grille affichée
    private int sizeY;

    // icones affichées dans la grille
    private ImageIcon icoHero;
    private ImageIcon icoVide;
    private ImageIcon icoMur;
    private ImageIcon icoBloc;
    private ImageIcon icoCasewin;
    private ImageIcon icoGlass;
    private ImageIcon icoBlackhole;
    private ImageIcon icoVideGrass;
    
    //time
    private JLabel movesLabel;
    private JLabel timeLabel;
    private Timer timer;
    private int time;
    //sound
    private Clip clip;


    



    private JLabel[][] tabJLabel; // cases graphique (au moment du rafraichissement, chaque case va être associée à une icône, suivant ce qui est présent dans le modèle)

    //Initialiser les deux états du jeux: Menu et Jeu
    private enum Etat {Menu, Jeu, Gagner};
    private Etat etat = Etat.Menu;

    
    public VueControleur(Jeu _jeu) {
        sizeX = jeu.SIZE_X;
        sizeY = _jeu.SIZE_Y;
        jeu = _jeu;

        jeu.addObserver(this);

        placerLesComposantsGraphiquess();

    }

    //private JLabel[][] tabJLabel;
    private JComponent grilleJLabels; // add this line
    private KeyAdapter keyAdapter; // Add this line

    // Cette méthode configure les composants graphiques en fonction de l'état actuel du jeu
    private void placerLesComposantsGraphiquess() {
        if (etat == Etat.Menu) {
            afficherMenu();
            //mettreAJourAffichage();
        } else if (etat == Etat.Jeu) {
            afficherJeu();
            //mettreAJourAffichage();
        } else if (etat == Etat.Gagner) {
            
            afficherGagner();
            //mettreAJourAffichage();
        }
    }

    // Cette méthode affiche une boîte de dialogue lorsque le jeu est gagné
    private void afficherGagner() {
        if ( clip != null) {
            clip.stop();
        }

        if (timer != null) {
            timer.stop();
            timer = null;
            remove(movesLabel);
            remove(timeLabel);
        }

        if (grilleJLabels != null) {
            remove(grilleJLabels);
            grilleJLabels = null; // Set to null after removing
        }

        revalidate(); // Update the container's layout
        repaint(); // Redraw the container's contents

            int result = JOptionPane.showOptionDialog(this, "You won", "Game Over", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);

        if (result == JOptionPane.OK_OPTION) {
            System.exit(0);
        };

    }

    // Cette méthode joue la musique du jeu
    public void GameMusic() {
        // Charge le fichier audio
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("gameMusic.wav"));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            if (clip != null) {
                clip.setFramePosition(0); // Rembobine le son au début
                clip.start(); // Commence à jouer le son
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

    }

    // Cette méthode affiche le menu du jeu
    private void afficherMenu() {
        if ( clip != null) {
            clip.stop();
        }


        if (grilleJLabels != null ) { // add this null check
            remove(grilleJLabels);
        }

        if (timer != null) {
            timer.stop();
            timer = null;
            remove(movesLabel);
            remove(timeLabel);
        }

        setTitle("Sokoban");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // permet de terminer l'application à la fermeture de la fenêtre

        grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        tabJLabel = new JLabel[sizeX][sizeY];

        
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    JLabel jlab = new JLabel();
                    tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                    grilleJLabels.add(jlab);
                }
            }
            add(grilleJLabels);

        JPanel ecranDemarrage = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image background = ImageIO.read(new File("Images/background.jpg"));
                    g.drawImage(background, 0, 0, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        JButton demarrerButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon icon = (ImageIcon) getIcon();
                if (icon != null) {
                    Image img = icon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
                    g.drawImage(img, 0, 0, this);
                }
            }
        };
        JButton quitterButton = new JButton();
        JButton restartButton = new JButton();

        ImageIcon icoDemarer = new ImageIcon("Images/start.png");

        int width = icoDemarer.getIconWidth() * (1);
        int height = icoDemarer.getIconHeight() * (1);
        
        Image resizedImage = icoDemarer.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        ImageIcon icoQuit = new ImageIcon("Images/quit.png");

        int widthicoQuit = icoQuit.getIconWidth() * (1);
        int heighticoQuit = icoQuit.getIconHeight() * (1);
        
        Image resizedImageicoQuit = icoQuit.getImage().getScaledInstance(widthicoQuit, heighticoQuit, Image.SCALE_SMOOTH);
        ImageIcon resizedIconicoQuit = new ImageIcon(resizedImageicoQuit);

        ImageIcon icoRestart = new ImageIcon("Images/restart.png");

        int widthicoicoRestart = icoRestart.getIconWidth() * (1);
        int heighticoicoRestart = icoRestart.getIconHeight() * (1);
        
        Image resizedImageicoRestart = icoRestart.getImage().getScaledInstance(widthicoicoRestart, heighticoicoRestart, Image.SCALE_SMOOTH);
        ImageIcon resizedIconicoRestart = new ImageIcon(resizedImageicoRestart);

        demarrerButton.setIcon(resizedIcon);
        quitterButton.setIcon(resizedIconicoQuit);
        restartButton.setIcon(resizedIconicoRestart);

        demarrerButton.setContentAreaFilled(false);
        demarrerButton.setBorderPainted(false);
        demarrerButton.setFocusPainted(false);

        quitterButton.setContentAreaFilled(false);
        quitterButton.setBorderPainted(false);
        quitterButton.setFocusPainted(false);

        restartButton.setContentAreaFilled(false);
        restartButton.setBorderPainted(false);
        restartButton.setFocusPainted(false);

        demarrerButton.addActionListener(e -> {

            etat = Etat.Jeu;
            placerLesComposantsGraphiquess();


            ecranDemarrage.setVisible(false);
        });

        quitterButton.addActionListener(e -> {
            System.exit(0);
        });

        restartButton.addActionListener(e -> {
            jeu.initialisationNiveauX(jeu.getLevel());
            etat = Etat.Jeu;
            placerLesComposantsGraphiquess();


            ecranDemarrage.setVisible(false);
            
        });

        ecranDemarrage.setLayout(new GridLayout(3, 1)); // set layout to 3 rows and 1 column
        demarrerButton.setBorder(BorderFactory.createEmptyBorder(50, 0, 10, 250));
        quitterButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 200));
        restartButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 150));

        ecranDemarrage.add(demarrerButton);
        ecranDemarrage.add(quitterButton);
        ecranDemarrage.add(restartButton);
        add(ecranDemarrage);

        mettreAJourAffichage();
            
    }

    // Cette méthode affiche le jeu
    private void afficherJeu() {
        if (grilleJLabels != null) { // add this null check
            remove(grilleJLabels);
        }


        
        chargerLesIcones(); //Affiche les element du jeu

        if (keyAdapter == null) {
            ajouterEcouteurClavier(); //Ajouter lecouteur au clavier
        }

        grilleJLabels = new JPanel(new GridLayout(sizeY, sizeX)); // grilleJLabels va contenir les cases graphiques et les positionner sous la forme d'une grille

        tabJLabel = new JLabel[sizeX][sizeY];

        
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    JLabel jlab = new JLabel();
                    tabJLabel[x][y] = jlab; // on conserve les cases graphiques dans tabJLabel pour avoir un accès pratique à celles-ci (voir mettreAJourAffichage() )
                    grilleJLabels.add(jlab);
                }
            }
            add(grilleJLabels);

        requestFocusInWindow(); //Trés important, re transmettre le focus sur le clavier

        // Stop the timer and remove the labels if they exist
        if (timer != null) {
            timer.stop();
            timer = null;
            remove(movesLabel);
            remove(timeLabel);
        }

        time=0;jeu.setMoves(0);
        movesLabel = new JLabel("Moves: 0");
        timeLabel = new JLabel("Time: 0s");
        add(movesLabel, BorderLayout.SOUTH);
        add(timeLabel, BorderLayout.NORTH);

        timer = new Timer(1000, e -> {
            time++;
            timeLabel.setText("Time: " + time + "s");
         });
        timer.start();
        GameMusic();
        mettreAJourAffichage();
    }


    // Cette méthode ajoute un écouteur de clavier pour gérer les événements du clavier
    private void ajouterEcouteurClavier() {
        keyAdapter = new KeyAdapter() { // new KeyAdapter() { ... } est une instance de classe anonyme, il s'agit d'un objet qui correspond au controleur dans MVC
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {  // on regarde quelle touche a été pressée

                    case KeyEvent.VK_LEFT : 
                        icoHero = chargerIcone("Images/player_12.png");
                        jeu.deplacerHeros(Direction.gauche);
                         break;
                    case KeyEvent.VK_RIGHT : 
                        icoHero = chargerIcone("Images/player_09.png");
                        jeu.deplacerHeros(Direction.droite);break;
                    case KeyEvent.VK_DOWN : 
                        icoHero = chargerIcone("Images/player_04.png");
                        jeu.deplacerHeros(Direction.bas); 
                        break;
                    case KeyEvent.VK_UP : 
                        icoHero = chargerIcone("Images/player_01.png");
                        jeu.deplacerHeros(Direction.haut);
                         break;
                    case KeyEvent.VK_M :
                        if (etat == Etat.Jeu) { // Only allow transition to menu if the game is currently running
                            etat = Etat.Menu; 
                            placerLesComposantsGraphiquess(); 
                        }
                        break;
                }
                jeu.setMoves(jeu.getMoves()+1);
                movesLabel.setText("Moves: " + jeu.getMoves());
            }
        };
        addKeyListener(keyAdapter);
    }

    // Cette méthode charge les icônes pour les éléments du jeu
    private void chargerLesIcones() {
        icoHero = chargerIcone("Images/player_05.png");
        icoVide = chargerIcone("Images/ground_06.png");
        icoMur = chargerIcone("Images/block_08.png");
        icoBloc = chargerIcone("Images/crate_02.png");
        icoCasewin = chargerIcone("Images/ground_04.png");
        icoGlass = chargerIcone("Images/Glass.png");
        icoBlackhole = chargerIcone("Images/Blackhole.png");
        icoVideGrass = chargerIcone("Images/ground_05.png");

    }

    // Cette méthode charge une icône à partir d'un fichier
    private ImageIcon chargerIcone(String urlIcone) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(urlIcone));
        } catch (IOException ex) {
            Logger.getLogger(VueControleur.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        return new ImageIcon(image);
    }

    
    /**
     * Il y a une grille du côté du modèle ( jeu.getGrille() ) et une grille du côté de la vue (tabJLabel)
     */
    // Cette méthode met à jour l'affichage en fonction de l'état du jeu
    private void mettreAJourAffichage() {
        if(jeu.isWin()){
            etat = Etat.Gagner;
            placerLesComposantsGraphiquess();
        }

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {

                Case c = jeu.getGrille()[x][y];

                if (c != null) {

                    Entite e = c.getEntite();

                    if (e!= null) {
                        if (c.getEntite() instanceof Heros) {
                            tabJLabel[x][y].setIcon(icoHero);
                        } else if (c.getEntite() instanceof Bloc) {
                            tabJLabel[x][y].setIcon(icoBloc);
                        }
                    } else {
                        if (jeu.getGrille()[x][y] instanceof Mur) {
                            tabJLabel[x][y].setIcon(icoMur);
                        } else if (jeu.getGrille()[x][y] instanceof Vide) {

                            tabJLabel[x][y].setIcon(icoVide);
                        }   else if (jeu.getGrille()[x][y] instanceof Casewin) {

                            tabJLabel[x][y].setIcon(icoCasewin);
                        }
                            else if (jeu.getGrille()[x][y] instanceof Glass) {
                            tabJLabel[x][y].setIcon(icoGlass);
                    }        
                            else if (jeu.getGrille()[x][y] instanceof Blackhole) {
                            tabJLabel[x][y].setIcon(icoBlackhole);
                    }
                            else if (jeu.getGrille()[x][y] instanceof Grass) {
                            tabJLabel[x][y].setIcon(icoVideGrass);
                    }

                }

            }
        }
    }
}

    // Cette méthode est appelée lorsque l'objet observé est mis à jour
    @Override
    public void update(Observable o, Object arg) {
        mettreAJourAffichage();

    }

    }

