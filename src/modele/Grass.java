package modele;

public class Grass extends Case {

    public Grass(Jeu _jeu) { super(_jeu); }

    @Override
    public boolean peutEtreParcouru() {
        return e == null;
    }
}
