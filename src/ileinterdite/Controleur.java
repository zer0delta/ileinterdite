package ileinterdite;

import model.CarteBleue;
import model.CarteOrange;
import model.Grille;
import model.Trésor;
import model.Aventurier;
import java.util.*;
import model.Explorateur;
import model.Ingénieur;
import model.Messager;
import model.Navigateur;
import model.Pilote;
import model.Plongeur;
import model.Tuile;
import util.*;
//import util.Utils.Pion;
import view.VueAventurier;

public class Controleur implements Observateur {

    private Grille grille;
    private int niveauEau;
    private VueAventurier[] ihm;
    private Trésor[] trésors;
    private ArrayList<Aventurier> joueurs;
    private ArrayList<CarteBleue> piocheBleues;
    private ArrayList<CarteBleue> defausseBleues;
    private ArrayList<CarteOrange> piocheOranges;
    private ArrayList<CarteOrange> defausseOranges;

    @Override
    public void traiterMessage(Message m) {
    }
    public void gererDeplacement(Aventurier joueur) {
        if (joueur.getCouleur() == Utils.Pion.JAUNE && ) {
            for (int i = 1; i < 3; i++) {
                joueur.calculTuileDispo(grille);
            }

        } else {
            joueur.calculTuileDispo(grille);
        }
    }
    public void gererAssechement(Aventurier joueur) {
        if (joueur.getCouleur() == Utils.Pion.ROUGE && ) {
            for (int i = 1; i < 3; i++) {
                joueur.calculTuileAss(grille);
            }
        } else {
            joueur.calculTuileAss(grille);
        }
    }
    public void gererDonnation() {
    }
    public void gererGainTresor() {
    }
    public void piocheCarteOrange() {
    }
    public void gererCarteOrange() {
    }
    public void piocheCarteBleue() {
    }
    public void gererCarteBleue() {

    }
    public void bouger(int ligne, int colonne) {
    }
    public void calculTouteTuileDispo(Grille g) {
    }

    public Controleur() {
        ArrayList<Tuile> Tuiles = new ArrayList();
        for (int i = 1; i < 24; i++) {
            Tuiles.add(new Tuile(NomTuile.getFromNb(i)));
        }
        
        //Initialisation de la Grille
        grille = new Grille(Tuiles);

        //Création des Trésors
        trésors[1] = new Trésor(NomTresor.LE_CRISTAL_ARDENT);
        trésors[2] = new Trésor(NomTresor.LA_PIERRE_SACREE);
        trésors[3] = new Trésor(NomTresor.LA_STATUE_DU_ZEPHYR);
        trésors[4] = new Trésor(NomTresor.LE_CALICE_DE_L_ONDE);
        
        /*
                INTERFACE TEXTE.
        */
        //Declaration de variable utiles
        Scanner sc = new Scanner(System.in);
        String choix = "0";
        int nbJoueurs = 2;
        String[] nomJoueurs = null;
        
        //Choix de l'utilisateur du nombre de joueurs à jouer la partie (max 4)
        do {
        System.out.println("Combien de joueurs vont jouer ? Faites un choix (entier entre 2 et 4) : ");
        choix = sc.nextLine();
            switch (choix) {
                case "2": {nbJoueurs=2;break;}
                case "3": {nbJoueurs=3;break;}
                case "4": {nbJoueurs=4;break;}
                case "0": return; default:
                                    System.out.println("Choix non valide");
                                    break;
                }
        } while (choix != "0");
        
        //sélection des noms de joueurs.
        for (int i = 1 ; i < nbJoueurs ; i++){
            System.out.println("Nom joueur n°"+i+" : ");
            choix = sc.nextLine();
            nomJoueurs[i]=choix;
        }
        
        //Création des Aventuriers.
        joueurs.add(new Pilote(grille.getTuile(4,3), Pion.BLEU));
        joueurs.add(new Navigateur(grille.getTuile(4,2), Pion.JAUNE));
        joueurs.add(new Ingénieur(grille.getTuile(4,1), Pion.ROUGE));
        joueurs.add(new Explorateur(grille.getTuile(5,3), Pion.VERT));
        joueurs.add(new Messager(grille.getTuile(2,3), Pion.BLANC));
        joueurs.add(new Plongeur(grille.getTuile(3,2), Pion.NOIR));
        //Mélange de ceux-ci dans joueurs.
        joueurs = Utils.melangerAventuriers(joueurs);
        
        //Création d'une vue pour chaque aventurier...
        for (int i = 1 ; i < nbJoueurs ; i++){
            ihm[i] = new VueAventurier(nomJoueurs[i], joueurs.get(i-1).getNomAventurier(), joueurs.get(i-1).getCouleur().getCouleur());
        }
        
        //boucle du jeu
        boolean fin = false;
        while (!fin){
            
            
            for (int i = 1 ; i < nbJoueurs ; i++){
                if (trésors[i].isGagne()){
                    
                }
            }
        }
        
    }

    public static void main(String[] args) {
        new Controleur();
    }
}
