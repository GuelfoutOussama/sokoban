package modele;

public class Glass extends Case {

    public Glass(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean peutEtreParcouru() {
        return true;
    }
}