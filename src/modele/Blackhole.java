package modele;

public class Blackhole extends Case {

    public Blackhole(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean peutEtreParcouru() {
        return true;
    }
}