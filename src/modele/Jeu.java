/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modele;


import java.awt.Point;
import java.awt.PopupMenu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Point;

// The main game class
public class Jeu extends Observable {
    // Constants for the size of the game grid
    public static final int SIZE_X = 20;
    public static final int SIZE_Y = 10;
    // Game state variables
    private boolean win;
    private String level;
    private Heros heros;
    private int moves;  // Number of moves made
    private int suiv;  // The next level to load
    private ArrayList<Point> tabblack =new ArrayList<Point>(); 
        
       
    
    // Maps for game entities
    private HashMap<Case, Point> map = new  HashMap<Case, Point>(); // permet de récupérer la position d'une case à partir de sa référence
    private Case[][] grilleEntites = new Case[SIZE_X][SIZE_Y]; // permet de récupérer une case à partir de ses coordonnées



    public Jeu() {
        initialisationNiveausuiv(suiv);
        //initialisationNiveauX("lvl0.txt");
    }
    // Getters and setters for game state variables
    public boolean isWin() {
        return win;
    }   
    public void SetWin(boolean win){
        this.win = win;
    }
    public String getLevel() {
        return level;
    }
    public int getMoves() {
        return moves;
    }
    public void setMoves(int moves) {
        this.moves = moves;
    }
    public Case[][] getGrille() {
        return grilleEntites;
    }
    
    public Heros getHeros() {
        return heros;
    }
    // Method to move the hero in a given direction
    public void deplacerHeros(Direction d) {
        
        
        heros.avancerDirectionChoisie(d);
        Case c =getGrille()[i][j];
        if (c instanceof Casewin) {
            Casewin c1 = (Casewin) c;
            if (c1.getWinners() == 2) {        
                c1.setWinners(0);
                moves =-1;
                suiv++;
                
                if(suiv == 4){
                    this.SetWin(true);
                }
                
                initialisationNiveausuiv(suiv);

                
                
                
            }
        }

        
        setChanged();
        notifyObservers();
    }

    int i;
    int j;

    // Method to initialize a level from a file
    public void initialisationNiveauX(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int y = 0;
            while ((line = reader.readLine()) != null) {
                for (int x = 0; x < line.length(); x++) {
                    char c = line.charAt(x);
                    switch (c) {
                        case 'M':
                            addCase(new Mur(this), x, y);
                            break;
                        case 'V':
                            addCase(new Vide(this), x, y);
                            break;
                            case 'Y':
                            addCase(new Grass(this), x, y);
                            break;
                        case 'H':
                            if(filename == "lvl1.txt" ||  filename == "lvl3.txt"){
                                grilleEntites[x][y] = new Grass(this);
                            }
                            else{
                                grilleEntites[x][y] = new Vide(this);
                            }
                            addCase(grilleEntites[x][y], x, y);
                            heros = new Heros(this, grilleEntites[x][y]);
                            break;
                        case 'B':
                            if(filename == "lvl1.txt" ||  filename == "lvl3.txt"){
                                grilleEntites[x][y] = new Grass(this);
                            }
                            else{
                                grilleEntites[x][y] = new Vide(this);
                            }
                            addCase(grilleEntites[x][y], x, y);
                            Bloc b = new Bloc(this, grilleEntites[x][y]);
                            break;
                        case 'G':
                            addCase(new Glass(this), x, y);
                            break;
                        case 'X':
                            addCase(new Blackhole(this), x, y);
                            Point p = new Point(x, y);
                            tabblack.add(p);


                            break;
                        case 'W':
                            addCase(new Casewin(this), x, y);
                            i=x;
                            j=y; 
                    }
                }
                y++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Method to initialize the next level
    public void initialisationNiveausuiv(int suiv){
        if(suiv == 0){
            level = "lvl0.txt";
            initialisationNiveauX("lvl0.txt");
        }
        if(suiv == 1){
            level = "lvl1.txt";
            initialisationNiveauX("lvl1.txt");
        }
        if(suiv == 2) {
            level = "lvl2.txt";
            initialisationNiveauX("lvl2.txt");
        }
        if(suiv == 3) {
            level = "lvl3.txt";
            initialisationNiveauX("lvl3.txt");
        }


    }


    // Method to initialize a level
    private void initialisationNiveau() {




        // murs extérieurs horizontaux
        for (int x = 0; x < 20; x++) {
            addCase(new Mur(this), x, 0);
            addCase(new Mur(this), x, 9);
        }

        // murs extérieurs verticaux
        for (int y = 1; y < 9; y++) {
            addCase(new Mur(this), 0, y);
            addCase(new Mur(this), 19, y);
        }

        for (int x = 1; x < 19; x++) {
            for (int y = 1; y < 9; y++) {
                addCase(new Vide(this), x, y);
            }

        }

        heros = new Heros(this, grilleEntites[4][4]);
        Bloc b = new Bloc(this, grilleEntites[6][6]);
    }
    // Method to add a case to the game grid
    private void addCase(Case e, int x, int y) {
        grilleEntites[x][y] = e;
        map.put(e, new Point(x, y));
    }
    

    
    /** Si le déplacement de l'entité est autorisé (pas de mur ou autre entité), il est réalisé
     * Sinon, rien n'est fait.
     */
    // Method to move an entity in a given direction
    public boolean deplacerEntite(Entite e, Direction d) {
        boolean retour = true;
        


        Point pCourant = map.get(e.getCase());
        
        

        if (pCourant == null) {
            System.out.println("c'est null");
        }

        Point pCible = calculerPointCible(pCourant, d);
        Case c = grilleEntites[pCible.x][pCible.y];
        if(c instanceof Glass){
            pCible = calculerPointCible(pCible, d);
        }
        if(c instanceof Blackhole){
            if(tabblack.get(0).getX() == pCible.getX() && tabblack.get(0).getY() == pCible.getY()){
                pCible = tabblack.get(1);
            }
            else{
                pCible = tabblack.get(0);
            }
        }

        if (contenuDansGrille(pCible)) {
            Entite eCible = caseALaPosition(pCible).getEntite();
            if (eCible != null) {
                eCible.pousser(d);
            }

            // si la case est libérée
            if (caseALaPosition(pCible).peutEtreParcouru()) {
                e.getCase().quitterLaCase();
                caseALaPosition(pCible).entrerSurLaCase(e);

            } else {
                retour = false;
            }

        } else {
            retour = false;
        }

        return retour;
    }
    
    // Method to calculate the target point for a move
    private Point calculerPointCible(Point pCourant, Direction d) {
        Point pCible = null;
        
        switch(d) {
            case haut: pCible = new Point(pCourant.x, pCourant.y - 1); break;
            case bas : pCible = new Point(pCourant.x, pCourant.y + 1); break;
            case gauche : pCible = new Point(pCourant.x - 1, pCourant.y); break;
            case droite : pCible = new Point(pCourant.x + 1, pCourant.y); break;     
            
        }

        
        return pCible;
    }

    

    
    /** Indique si p est contenu dans la grille
     */
    // Method to check if a point is within the game grid
    private boolean contenuDansGrille(Point p) {
        return p.x >= 0 && p.x < SIZE_X && p.y >= 0 && p.y < SIZE_Y;
    }
    // Method to get the case at a given point
    private Case caseALaPosition(Point p) {
        Case retour = null;
        
        if (contenuDansGrille(p)) {
            retour = grilleEntites[p.x][p.y];
        }
        
        return retour;
    }

}