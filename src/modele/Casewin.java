package modele;

public class Casewin extends Case {
    public static int winners;
    public boolean win = false;
    public int getWinners() {      ///modif
        return winners;
    }
    public void setWinners(int i){     //modif
        winners=i;
    }
    public Casewin(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean peutEtreParcouru() {
        return e == null;
    }
    @Override
    public boolean entrerSurLaCase(Entite e) {
        setEntite(e);
        if (e instanceof Bloc) {
            win = true;
            winners++;
        }
        return win;
    }
    @Override
    public void quitterLaCase() {
        if (e instanceof Bloc) {
            win = false; 
            winners--;
            
        }
        e = null;

    }

}