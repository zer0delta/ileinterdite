package model;

import model.Aventurier;
import java.util.ArrayList;
import util.Utils;

public class Explorateur extends Aventurier {

    public Explorateur(Tuile t, Utils.Pion p) {
        super(t, p);
    }

    @Override
    public ArrayList calculTuileDispo(Grille g) {
        ArrayList<Tuile> tuileDispo = new ArrayList<Tuile>();
        int c = this.getTuile().getColonne();
        int l = this.getTuile().getLigne();

        if (g.getTuile(l
                - 1, c) != null && g.getTuile(l - 1, c).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispo.add(g.getTuile(l - 1, c));
        }

        if (g.getTuile(l, c
                - 1) != null && g.getTuile(l, c - 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispo.add(g.getTuile(l, c - 1));
        }

        if (g.getTuile(l
                + 1, c) != null && g.getTuile(l + 1, c).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispo.add(g.getTuile(l + 1, c));
        }

        if (g.getTuile(l, c
                + 1) != null && g.getTuile(l, c + 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispo.add(g.getTuile(l, c + 1));
        }

        if (g.getTuile(l + 1, c
                + 1) != null && g.getTuile(l + 1, c + 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispo.add(g.getTuile(l + 1, c + 1));
        }
        if (g.getTuile(l - 1, c
                + 1) != null && g.getTuile(l - 1, c + 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispo.add(g.getTuile(l - 1, c + 1));
        }
        if (g.getTuile(l + 1, c
                - 1) != null && g.getTuile(l + 1, c - 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispo.add(g.getTuile(l + 1, c - 1));
        }
        if (g.getTuile(l - 1, c
                - 1) != null && g.getTuile(l - 1, c - 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispo.add(g.getTuile(l - 1, c - 1));
        }
        return tuileDispo;
    }

    /**
     *
     * @param tDepart
     * @param g
     */
    public ArrayList calculTuileAss(Grille g) {
        ArrayList<Tuile> tuileDispoAss = new ArrayList<Tuile>();
        int c = this.getTuile().getColonne();
        int l = this.getTuile().getLigne();

        if (g.getTuile(l
                - 1, c) != null && g.getTuile(l - 1, c).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispoAss.add(g.getTuile(l - 1, c));
        }

        if (g.getTuile(l, c
                - 1) != null && g.getTuile(l, c - 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispoAss.add(g.getTuile(l, c - 1));
        }

        if (g.getTuile(l
                + 1, c) != null && g.getTuile(l + 1, c).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispoAss.add(g.getTuile(l + 1, c));
        }

        if (g.getTuile(l, c
                + 1) != null && g.getTuile(l, c + 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispoAss.add(g.getTuile(l, c + 1));
        }

        if (g.getTuile(l + 1, c
                + 1) != null && g.getTuile(l + 1, c + 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispoAss.add(g.getTuile(l + 1, c + 1));
        }
        if (g.getTuile(l - 1, c
                + 1) != null && g.getTuile(l - 1, c + 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispoAss.add(g.getTuile(l - 1, c + 1));
        }
        if (g.getTuile(l + 1, c
                - 1) != null && g.getTuile(l + 1, c - 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispoAss.add(g.getTuile(l + 1, c - 1));
        }
        if (g.getTuile(l - 1, c
                - 1) != null && g.getTuile(l - 1, c - 1).getEtat() != Utils.EtatTuile.COULEE) {
            tuileDispoAss.add(g.getTuile(l - 1, c - 1));
        }
        return tuileDispoAss;
    }
}
