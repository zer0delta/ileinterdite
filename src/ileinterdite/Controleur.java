package ileinterdite;

import model.*;
import util.*;
import util.Utils.Pion;
import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import view.VueAccueil;
import view.VueAventurier;

/*
    Groupe C2 :  Prapant.B, Labartino.Y, Giroud.T, Malod.V
 */
public class Controleur implements Observateur {

    /*
    Note :  pour une lecture agréable du code, nous vous invitons à
            réduire depuis netbeans chaque méthode pour n'en apercevoir
            que le titre et les paramètres de celles-ci.
     */
    private VueAccueil accueil;
    private VueAventurier ihm;

    private Grille grille;
    private Tresor[] trésors;
    private HashMap<Aventurier, String> nomJoueurs;
    private ArrayList<Aventurier> joueurs;
    private Aventurier joueurCourant, joueurEnDetresse;
    private Stack<CarteBleue> piocheBleues;
    private ArrayList<CarteBleue> defausseBleues;
    private Stack<CarteOrange> piocheOranges;
    private ArrayList<CarteOrange> defausseOranges;
    
    private boolean pouvoirPiloteDispo, pouvoirIngénieurUsé, jeuEnCours, àPioché, sauvetageEnCours;
    private int niveauEau, nbJoueurs, nbActions;
    private CarteOrange carteTMP;

    @Override
    public void traiterMessage(Message m) {
        switch (m.type) {
            case COMMENCER:
                this.accueil.fermer();
                Parameters.setLogs(m.logs);
                Parameters.setAleas(m.aleas);
                this.niveauEau = m.difficulté;
                this.nbJoueurs = m.nbJoueurs;
                iniJeu();
                this.nomJoueurs.putAll(m.joueurs);
                debutJeu();
                getIHM().afficherEtatAction(ihm.ETAT_COMMENCER, getNomJoueurs().get(getJoueurCourant()), getNbAction());
                break;
            case SOUHAITE_DEPLACEMENT:
                if (deplacementPossible(getJoueurCourant())) {
                    gererDeplacement(getJoueurCourant());
                    getIHM().afficherEtatAction(ihm.ETAT_SOUHAITE_DEPLACEMENT, getNomJoueurs().get(getJoueurCourant()), getNbAction());
                }
                break;
            case ACTION_DEPLACEMENT:
                if (!sauvetageEnCours) {
                    deplacement(getJoueurCourant(), m.tuile);
                } else {
                    deplacement(this.joueurEnDetresse, m.tuile);
                }
                break;
            case SOUHAITE_ASSECHER:
                if (assechementPossible()) {
                    gererAssechement();
                    getIHM().afficherEtatAction(ihm.ETAT_SOUHAITE_ASSECHER, getNomJoueurs().get(getJoueurCourant()), getNbAction());
                }
                break;
            case ACTION_ASSECHER:
                assechement(m.tuile);
                break;
            case SOUHAITE_DONNER:
                if (donationPossible()) {
                    getIHM().selectionCarte();
                    getIHM().afficherEtatAction(ihm.ETAT_SOUHAITE_DONNER, getNomJoueurs().get(getJoueurCourant()), getNbAction());
                }
                break;
            case DONNER_CARTE:
                gererDonation(m.numCarte);
                break;
            case ACTION_DONNER:
                donation(m.receveur);
                break;
            case ACTION_GAGNER_TRESOR:
                gererGainTresor();
                break;
            case SELECTIONNER_CARTE:
                gererCarteSelect(m.numCarte);
                break;
            case SOUHAITE_JOUER_SPECIALE:
                gererCarteSpecial();
                break;
            case JOUER_SPECIALE:
                jouerSpecial(m.numCarte);
                break;
            case ASSECHER:
                if (m.tuile.getSelected() == 1) {
                    m.tuile.setEtat(Utils.EtatTuile.ASSECHEE);
                    getJoueurCourant().defausseCarte(carteTMP);
                    addDefausseOranges(carteTMP);
                    actualiserJeu();
                }
                break;
            case VOIR_JOUEUR:
                voirJoueur(m.joueurAffiché);
                break;
            case FINIR_TOUR:
                this.nbActions = 0;
                if (Parameters.LOGS) {
                    System.out.println("\t\033[31mFin du tour\033[0m");
                }
                actualiserJeu();
                break;
            case ANNULER:
                if (Parameters.LOGS) {
                    System.out.println("\033[31mAnulation\033[0m");
                }
                actualiserJeu();
                break;
            case QUITTER :
                if (Parameters.LOGS) {
                    System.out.println("\n\n\t\t\033[31;45;4;21mFermeture du jeu\033[0m\n\n");
                }
                getIHM().quitter();
        }
    }

    //METHODES DE GESTION DU JEU
    private boolean deplacementPossible(Aventurier joueur) {
        boolean deplDispo = true;
        Grille g = getGrille();
        ArrayList<Tuile> tuilesDispo = new ArrayList<Tuile>();
        if (joueur.getCouleur() == Utils.Pion.BLEU && pouvoirPiloteDispo) {
            return deplDispo;
        } else {
            tuilesDispo = joueur.calculTuileDispo(g);
            if (!tuilesDispo.isEmpty()) {
                return deplDispo;
            }
        }
        return !deplDispo;
    }
    private void gererDeplacement(Aventurier joueur) {
        Grille g = getGrille();
        ArrayList<Tuile> tuilesDispo = new ArrayList<Tuile>();
        ArrayList<Tuile> tuilesPilote = new ArrayList<Tuile>();
        tuilesDispo.addAll(joueur.calculTuileDispo(g));
        g.selectionTuileDispo(tuilesDispo, 1);
        getIHM().afficherTuilesDispo();
        if (joueur.getCouleur() == Utils.Pion.BLEU && pouvoirPiloteDispo) {
            tuilesPilote.addAll(calculTouteTuileDispo());
            tuilesPilote.removeAll(tuilesDispo);
            tuilesPilote.remove(joueur.getTuile());
            g.selectionTuileDispo(tuilesPilote, 2);
            getIHM().afficherTuilesPilote();
        }
    }
    private void deplacement(Aventurier joueur, Tuile t){
        if (t.getSelected() != 0) {
            if (t.getSelected() == 2) {
                pouvoirPiloteDispo = false;
            }
            joueur.seDeplace(t);
            if (Parameters.LOGS) {
                System.out.println("\t\033[32;46mLe joueur "+getNomJoueurs().get(joueur)+" s'est déplacé sur la tuile "+t.getNom().toString()+".\033[0m");
            }
            if (!sauvetageEnCours){
                this.nbActions--;
            }
        }
        actualiserJeu();
    }
    
    private void gererSauvetage(){
        String nomJoueur = "Bradley";
        Aventurier joueurSubmergé = null;
        boolean sauvetageNecessaire = false;
        for (Tuile t : getGrille().getGrille()) {
            if (t.getEtat() == Utils.EtatTuile.COULEE && !t.getPossede().isEmpty()) {
                nomJoueur = getNomJoueurs().get(t.getPossede().get(0));
                joueurSubmergé = t.getPossede().get(0);
                sauvetageNecessaire = true;
            }
        }
        if (sauvetageNecessaire) {
            if (Parameters.LOGS) {
                System.out.println("\t\033[31mSauvetage nécéssaire du joueur : "+nomJoueur+"\033[0m");
            }
            if (deplacementPossible(joueurSubmergé)) {
                getIHM().afficherEtatAction(ihm.ETAT_SAUVETAGE, nomJoueur, null);
                gererDeplacement(joueurSubmergé);
                this.joueurEnDetresse = joueurSubmergé;
                getIHM().afficherSauvetage();
            } else {
                this.sauvetageEnCours = false;
            }
        } else {
            this.sauvetageEnCours = false;
        }
    }

    private boolean assechementPossible() {
        ArrayList<Tuile> tuilesDispo = getJoueurCourant().calculTuileAss(getGrille());
        return (!tuilesDispo.isEmpty());
    }
    private void gererAssechement() {
        //méthode qui permet a un aventurier d'assécher une tuile
        Aventurier joueur = getJoueurCourant();
        Grille g = getGrille();
        ArrayList<Tuile> tuilesDispo = joueur.calculTuileAss(g);
        g.selectionTuileDispo(tuilesDispo, 1);
        getIHM().afficherTuilesDispo();
    }
    private void assechement(Tuile t) {
        if (t.getSelected() == 1) {
            if (getJoueurCourant().getCouleur() != Pion.ROUGE) {
                //application en règle générale pour les autres aventuriers que l'ingénieur
                t.setEtat(Utils.EtatTuile.ASSECHEE);
                if (Parameters.LOGS) {
                    System.out.println("\033[32mAsséchement de la tuile :\033[0m");
                    t.affiche();
                }
                this.nbActions--;
                actualiserJeu();
            } else if (!pouvoirIngénieurUsé) {
                //il s'agit de l'ingénieur et c'est son premier assèchement.
                pouvoirIngénieurUsé = true;
                t.setEtat(Utils.EtatTuile.ASSECHEE);
                if (Parameters.LOGS) {
                    System.out.println("\033[32mAsséchement de la tuile :\033[0m");
                    t.affiche();
                }
                this.nbActions--;
                if (assechementPossible()) {
                    gererAssechement();
                } else {
                    actualiserJeu();
                }
            } else {
                //c'est l'ingénieur mais il a déjà assècher une fois.
                t.setEtat(Utils.EtatTuile.ASSECHEE);
                if (Parameters.LOGS) {
                    System.out.println("\033[32mAsséchement de la tuile :\033[0m");
                    t.affiche();
                }
                actualiserJeu();
            }
        } else {
            actualiserJeu();
        }
    }

    private boolean donationPossible() {
        boolean donnDispo = true;
        Aventurier joueur = getJoueurCourant();
        if (!joueur.getMain().isEmpty()) {
            ArrayList<CarteOrange> cartesDispo = new ArrayList<CarteOrange>();
            for (CarteOrange c : joueur.getMain()) {
                if (!(c.getRole().equals("Helicoptere") || c.getRole().equals("Sac de sable"))) {
                    cartesDispo.add(c);
                }
            }
            if (!cartesDispo.isEmpty()) {
                Grille g = getGrille();
                ArrayList<Tuile> tuiles = g.getGrille();
                ArrayList<Aventurier> jDispo = new ArrayList<Aventurier>();
                //vérification s'il s'agit du messager pour son pouvoir
                if (joueur.getCouleur() == Pion.BLANC) {
                    for (Tuile t : tuiles) {
                        if (!t.getPossede().isEmpty()) {
                            for (Aventurier a : t.getPossede()) {
                                if (a.getMain().size() < 9) {
                                    jDispo.add(a);
                                }
                            }
                        }
                    }
                } else if (!joueur.getTuile().getPossede().isEmpty()) {
                    for (Aventurier a : joueur.getTuile().getPossede()) {
                        if (a.getMain().size() < 9) {
                            jDispo.add(a);
                        }
                    }
                }
                jDispo.remove(joueur);
                if (!jDispo.isEmpty()) {
                    return donnDispo;
                }
            }
        }
        return !donnDispo;
    }
    private void gererDonation(int numCarte) {
        if (getJoueurCourant().getMain().get(numCarte).getRole().equals("Trésor")) {
            carteTMP = getJoueurCourant().getMain().get(numCarte);
            getIHM().afficherEtatAction(ihm.ETAT_DONNER_CARTE, null, null);
            if (getJoueurCourant().getCouleur() == Pion.BLANC) {
                for (Tuile t : getGrille().getGrille()) {
                    if (getJoueurCourant().getTuile() == t && t.getPossede().size() > 1) {
                        t.setSelected(1);
                    } else if (getJoueurCourant().getTuile() != t && !t.getPossede().isEmpty()) {
                        t.setSelected(1);
                    }
                }
            } else if (getJoueurCourant().getTuile().getPossede().size() > 1) {
                getJoueurCourant().getTuile().setSelected(1);
            }
            getIHM().afficherTuilesDispo();
        } else {
            actualiserJeu();
        }
    }
    private void donation(Aventurier receveur) {
        if (getJoueurCourant() != receveur && receveur.getMain().size() < 9 && receveur != null) {
            getJoueurCourant().defausseCarte(carteTMP);
            if (Parameters.LOGS) {
                System.out.println("\033[32mDonation de la carte.\033[0m");
            }
            receveur.piocheCarte(carteTMP);
            nbActions--;
            getIHM().dessinCartes(getJoueurCourant().getMain());
        }
        actualiserJeu();
    }

    private void gererGainTresor() {
        Aventurier joueur = getJoueurCourant();
        if (joueur.getMain().size() >= 4) {
            int nbCarteTresorPS = 0;
            int nbCarteTresorSZ = 0;
            int nbCarteTresorCA = 0;
            int nbCarteTresorCO = 0;
            ArrayList<CarteTrésor> cartesTresors = new ArrayList<CarteTrésor>();
            for (CarteOrange c : joueur.getMain()) {
                if (c.getRole().equals("Trésor")) {
                    cartesTresors.add((CarteTrésor) c);
                }
            }
            for (CarteTrésor c : cartesTresors) {
                if (c.getNomTresor() == NomTresor.LA_PIERRE_SACREE) {
                    nbCarteTresorPS++;
                } else if (c.getNomTresor() == NomTresor.LA_STATUE_DU_ZEPHYR) {
                    nbCarteTresorSZ++;
                } else if (c.getNomTresor() == NomTresor.LE_CALICE_DE_L_ONDE) {
                    nbCarteTresorCO++;
                } else if (c.getNomTresor() == NomTresor.LE_CRISTAL_ARDENT) {
                    nbCarteTresorCA++;
                }
            }
            if ((nbCarteTresorCA | nbCarteTresorCO | nbCarteTresorPS | nbCarteTresorSZ) >= 4) {
                ArrayList<CarteOrange> cartesDuJoueur = new ArrayList<CarteOrange>();
                cartesDuJoueur.addAll(joueur.getMain());
                if (joueur.getTuile().getNom() == NomTuile.LE_TEMPLE_DU_SOLEIL || joueur.getTuile().getNom() == NomTuile.LE_TEMPLE_DE_LA_LUNE) {
                    if (nbCarteTresorPS >= 4) {
                        for (CarteOrange cO : cartesDuJoueur) {
                            for (int i = 0; i < 4; i++) {
                                CarteTrésor cTresor = cartesTresors.get(i);
                                if (cTresor.getNomTresor() == NomTresor.LA_PIERRE_SACREE && cO == cTresor) {
                                    joueur.getMain().remove(cO);
                                }
                            }
                        }
                        getTrésors()[0].setGagne(true);
                        if (Parameters.LOGS) {
                            System.out.println("\033[33mVous avez gagné le Trésor :\033[0m");
                            getTrésors()[0].affiche();
                        }
                    }
                } else if (joueur.getTuile().getNom() == NomTuile.LE_JARDIN_DES_HURLEMENTS || joueur.getTuile().getNom() == NomTuile.LE_JARDIN_DES_MURMURES) {
                    if (nbCarteTresorSZ >= 4) {
                        for (CarteOrange cO : cartesDuJoueur) {
                            for (int i = 0; i < 4; i++) {
                                CarteTrésor cTresor = cartesTresors.get(i);
                                if (cTresor.getNomTresor() == NomTresor.LA_STATUE_DU_ZEPHYR && cO == cTresor) {
                                    joueur.getMain().remove(cO);
                                }
                            }
                        }
                        getTrésors()[1].setGagne(true);
                        if (Parameters.LOGS) {
                            System.out.println("\033[mVous avez gagné le Trésor :\033[0m");
                            getTrésors()[1].affiche();
                        }
                    }
                } else if (joueur.getTuile().getNom() == NomTuile.LA_CAVERNE_DES_OMBRES || joueur.getTuile().getNom() == NomTuile.LA_CAVERNE_DES_OMBRES) {
                    if (nbCarteTresorCA >= 4) {
                        for (CarteOrange cO : cartesDuJoueur) {
                            for (int i = 0; i < 4; i++) {
                                CarteTrésor cTresor = cartesTresors.get(i);
                                if (cTresor.getNomTresor() == NomTresor.LE_CRISTAL_ARDENT && cO == cTresor) {
                                    joueur.getMain().remove(cO);
                                }
                            }
                        }
                        getTrésors()[2].setGagne(true);
                        if (Parameters.LOGS) {
                            System.out.println("\033[mVous avez gagné le Trésor :\033[0m");
                            getTrésors()[2].affiche();
                        }
                    }
                } else if (joueur.getTuile().getNom() == NomTuile.LE_PALAIS_DE_CORAIL || joueur.getTuile().getNom() == NomTuile.LE_PALAIS_DES_MAREES) {
                    if (nbCarteTresorCO >= 4) {
                        for (CarteOrange cO : cartesDuJoueur) {
                            for (int i = 0; i < 4; i++) {
                                CarteTrésor cTresor = cartesTresors.get(i);
                                if (cTresor.getNomTresor() == NomTresor.LE_CALICE_DE_L_ONDE && cO == cTresor) {
                                    joueur.getMain().remove(cO);
                                }
                            }
                        }
                        getTrésors()[3].setGagne(true);
                        if (Parameters.LOGS) {
                            System.out.println("\033[mVous avez gagné le Trésor :\033[0m");
                            getTrésors()[3].affiche();
                        }
                    }
                }
            }
        }
        getIHM().actualiserTrésor(getTrésors());
        if (Parameters.LOGS) {
            System.out.println("\t\033[33mNombre d'action(s) réstante(s) : \033[31;43m"+getNbAction()+"\033[0m");
        }
        actualiserJeu();
    }

    private int specialePossible() {
        // retourne : 0 si pas possible
        // 1 si carte Helicoptere
        // 2 si carte Sac de sable
        Grille g = getGrille();
        Aventurier joueur = getJoueurCourant();
        ArrayList<CarteOrange> cartesJoueur = new ArrayList<CarteOrange>();
        cartesJoueur.addAll(joueur.getMain());
        int nbJoueur = getNbJoueur();
        if (!joueur.getMain().isEmpty()) {
            if (g.getTuile(NomTuile.HELIPORT).getPossede().size() == nbJoueur) {
                boolean carteHelico = false;
                for (CarteOrange c : cartesJoueur) {
                    if (c.getRole().equals("Helicoptere")) {
                        carteHelico = true;
                    }
                }
                if (carteHelico) {
                    return 1;
                }
            } else {
                ArrayList<Tuile> tuilesInnondées = new ArrayList<Tuile>();
                for (Tuile t : g.getGrille()) {
                    if (t.getEtat() == Utils.EtatTuile.INONDEE) {
                        tuilesInnondées.add(t);
                    }
                }
                if (!tuilesInnondées.isEmpty()) {
                    boolean carteSac = false;
                    for (CarteOrange c : cartesJoueur) {
                        if (c.getRole().equals("Sac de sable")) {
                            carteSac = true;
                        }
                    }
                    if (carteSac) {
                        return 2;
                    }
                }
            }
        }
        return 0;
    }
    private void gererCarteSpecial() {
        if (specialePossible() == 1) {
            getIHM().afficheCartesHelico(getJoueurCourant().getMain());
            getIHM().afficherEtatAction(ihm.ETAT_SOUHAITE_JOUER_SPECIALE, getNomJoueurs().get(getJoueurCourant()), getNbAction());
            if (Parameters.LOGS) {
                System.out.println("\033[32mVous pouvez cliquer sur une carte Helicoptère pour la jouer\033[0m");
            }
        } else if (specialePossible() == 2) {
            getIHM().afficheCartesSac(getJoueurCourant().getMain());
            getIHM().afficherEtatAction(ihm.ETAT_SOUHAITE_JOUER_SPECIALE, getNomJoueurs().get(getJoueurCourant()), getNbAction());
            if (Parameters.LOGS) {
                System.out.println("\033[32mVous pouvez cliquer sur une carte Sac de sable pour la jouer\033[0m");
            }
        } else {
            actualiserJeu();
        }
    }
    private void jouerSpecial(int numCarte) {
        if (getJoueurCourant().getMain().get(numCarte).getRole().equals("Helicoptere") && specialePossible() == 1) {
            getJoueurCourant().defausseCarte(getJoueurCourant().getMain().get(numCarte));
            addDefausseOranges(getJoueurCourant().getMain().get(numCarte));
            this.jeuEnCours = false;
            getIHM().afficherEtatAction(ihm.ETAT_JOUEUR, getNomJoueurs().get(getJoueurCourant()), getNbAction());
        } else if (getJoueurCourant().getMain().get(numCarte).getRole().equals("Sac de sable") && specialePossible() == 2) {
            carteTMP = (CarteSacDeSable) getJoueurCourant().getMain().get(numCarte);
            getGrille().selectionTuileDispo(calculTouteTuileInnon(), 1);
            getIHM().afficherTuilesDispo();
            getIHM().afficherEtatAction(ihm.ETAT_SOUHAITE_ASSECHER, getNomJoueurs().get(getJoueurCourant()), getNbAction());
        }
    }

    private void gererCarteSelect(int numCarte) {
        if ((getJoueurCourant().getMain().size() - 1) >= numCarte) {
            CarteOrange c = getJoueurCourant().getMain().get(numCarte);
            if (Parameters.LOGS) {
                System.out.println("\033[32mVous avez sélectionnez cette carte :\033[0m");
                c.affiche();
            }
            if (specialePossible() == 1 && c.getRole().equals("Helicoptere")) {
                getJoueurCourant().defausseCarte(c);
                addDefausseOranges(c);
                this.jeuEnCours = false;
                getIHM().afficherEtatAction(ihm.ETAT_JOUEUR, getNomJoueurs().get(getJoueurCourant()), getNbAction());
            } else if (specialePossible() == 2 && c.getRole().equals("Sac de sable")) {
                carteTMP = (CarteSacDeSable) getJoueurCourant().getMain().get(numCarte);
                getGrille().selectionTuileDispo(calculTouteTuileInnon(), 1);
                getIHM().afficherTuilesDispo();
                getIHM().afficherEtatAction(ihm.ETAT_SOUHAITE_ASSECHER, getNomJoueurs().get(getJoueurCourant()), getNbAction());
            } else {
                getJoueurCourant().defausseCarte(c);
                addDefausseOranges(c);
                actualiserJeu();
            }
            getIHM().dessinCartes(getJoueurCourant().getMain());
        }
    }

    private boolean gererCarteOrange() {
        /*Méthode qui permet a un joueur de piocher deux cartes a la fin de son tour*/
        Aventurier joueur = getJoueurCourant();
        boolean mainTropPleine = true;
        boolean carteMDEpiochée = false;
        ArrayList<CarteOrange> cartesPiochées = new ArrayList<CarteOrange>();
        VueAventurier vue = getIHM();
        //On picohe deux cartes et on vérifie a chaque fois si la pioche est vide
        for (int i = 0; i < 2; i++) {
            cartesPiochées.add(piocheCarteOrange());
            if (getPiocheOranges().empty()) {
                Collections.shuffle(getDefausseOranges());
                for (CarteOrange c : getDefausseOranges()) {
                    addPiocheOrange(c);
                }
                viderDefausseOranges();
            }
        }
        if (Parameters.LOGS) {
            System.out.println("\033[33mVous avez pioché deux cartes oranges\033[0m");
        }
        //Pour les cartes les piochées, on vérifie si celle-ci sont des cartes "montée des eaux"
        for (CarteOrange c : cartesPiochées) {
            if (c.getRole().equals("Montée des eaux")) {
                this.niveauEau++;
                carteMDEpiochée = true;
                if (Parameters.LOGS) {
                    System.out.println("\033[36mCarte montée des eaux piochée, le niveau d'eau monte et passe à : \033[34;45m"+getNiveau()+"\033[0m");
                }
                addDefausseOranges(c);
            } else {
                joueur.piocheCarte(c);
            }
        }
        //Si la pioche de carte bleue n'est pas vide et qu'on a pioché au moins
        //une carte MDE alors on mélange puis remet la defausse de carte bleue sur le tas de pioche
        if (!getPiocheBleues().empty() && carteMDEpiochée) {
            Collections.shuffle(getDefausseBleues());
            for (CarteBleue b : getDefausseBleues()) {
                addPiocheBleue(b);
            }
            viderDefausseBleues();
            vue.actualiserNiveauEau(niveauEau);
        }
        //Si la main contient plus de 5 cartes
        if (joueur.getMain().size() > 5) {
            return mainTropPleine;
        }
        return !mainTropPleine;
    }
    private void gererCarteBleue() {
        /*Méthode qui permet au joueur de piocher des cartes innondation a la fin de son tour*/
        //On pioche un certain nombre de cartes en fonction du niveau d'eau
        int nbPioche;
        int nivEau = this.getNiveau();
        if (nivEau >= 8) {
            nbPioche = 5;
        } else if (nivEau >= 6) {
            nbPioche = 4;
        } else if (nivEau >= 3) {
            nbPioche = 3;
        } else {
            nbPioche = 2;
        }
        if (Parameters.LOGS) {
            System.out.println("\033[35mLe niveau d'eau s'élève à : "+nivEau+", vous piochez donc : "+nbPioche+" cartes\033[0m");
        }
        CarteBleue c;
        //On influence donc l'état de la tuile concernée par cette carte
        for (int i = 0; i < nbPioche; i++) {
            c = piocheCarteBleue();
            Tuile t = getGrille().getTuile(c.getInnonde().getNom());
            if (t.getEtat() == Utils.EtatTuile.ASSECHEE) {
                t.setEtat(Utils.EtatTuile.INONDEE);
                addDefausseBleues(c);
            } else if (t.getEtat() == Utils.EtatTuile.INONDEE) {
                t.setEtat(Utils.EtatTuile.COULEE);
                if (!t.getPossede().isEmpty()) {
                    this.sauvetageEnCours = true;
                }
            }
            if (getPiocheBleues().empty()) {
                if (Parameters.LOGS) {
                    System.out.println("\033[34;36mMélange de la défausse de carte bleues sur la pile\033[0m");
                }
                Collections.shuffle(getDefausseBleues());
                for (CarteBleue b : getDefausseBleues()) {
                    addPiocheBleue(b);
                }
                viderDefausseBleues();
            }
        }
    }

    private void iniJeu() {
        //initialisations des tableaux/vecteurs
        nomJoueurs = new HashMap<Aventurier, String>();
        trésors = new Tresor[4];
        piocheOranges = new Stack<CarteOrange>();
        defausseOranges = new ArrayList<CarteOrange>();
        piocheBleues = new Stack<CarteBleue>();
        defausseBleues = new ArrayList<CarteBleue>();
        joueurs = new ArrayList<Aventurier>();

        //initialisations
        iniTrésor();
        iniGrille();
        ihm = new VueAventurier(getGrille());
        ihm.addObservateur(this);
        iniCartes();
        if (Parameters.LOGS) {
            System.out.println("\033[31;44mJeu initialisé au complet.\033[0m");
        }
    }
    private void iniCartes() {
        //Création des cartes oranges (trésor)        
        ArrayList<CarteOrange> tmpOranges = new ArrayList<CarteOrange>();
        for (int i = 0; i < 3; i++) {
            tmpOranges.add(new CarteHelicoptere());
        }
        for (int i = 0; i < 5; i++) {
            tmpOranges.add(new CarteTrésor(NomTresor.LE_CRISTAL_ARDENT));
            tmpOranges.add(new CarteTrésor(NomTresor.LA_STATUE_DU_ZEPHYR));
            tmpOranges.add(new CarteTrésor(NomTresor.LE_CALICE_DE_L_ONDE));
            tmpOranges.add(new CarteTrésor(NomTresor.LA_PIERRE_SACREE));
        }
        for (int i = 0; i < 2; i++) {
            tmpOranges.add(new CarteMonteeDesEaux()); //2 cartes montée des eaux, modification : 18/06/2018 (m2107 consignes m.à.j.)
            tmpOranges.add(new CarteSacDeSable());
        }
        Collections.shuffle(tmpOranges);
        //Ajout de celles-ci dans la pioche orange.
        for (CarteOrange c : tmpOranges) {
            piocheOranges.push(c);
        }

        //Création des cartes bleues (innondation)        
        ArrayList<CarteBleue> tmpBleues = new ArrayList<CarteBleue>();
        for (Tuile t : getGrille().getGrille()) {
            tmpBleues.add(new CarteBleue(t));
        }
        Collections.shuffle(tmpBleues);
        //Ajout de celles-ci dans la pioche bleue.
        for (CarteBleue c : tmpBleues) {
            piocheBleues.push(c);
        }
        if (Parameters.LOGS) {
            System.out.println("\033[32mCartes initialisées\033[0m");
        }
    }
    private void iniGrille() {
        //Génération des 24 tuiles
        ArrayList<Tuile> Tuiles = new ArrayList<Tuile>();
        for (int i = 1; i < 25; i++) {
            Tuiles.add(new Tuile(NomTuile.getFromNb(i)));
        }
        //Initialisation de la Grille
        if (Parameters.ALEAS) { //ALEAS == true
            Collections.shuffle(Tuiles);
        }
        grille = new Grille(Tuiles);
        if (Parameters.LOGS) {
            System.out.println("\033[32mGrille initialisée\033[0m");
        }
    }
    private void iniTrésor() {
        //Création des Trésors
        trésors[0] = new Tresor(NomTresor.LA_PIERRE_SACREE);
        trésors[1] = new Tresor(NomTresor.LA_STATUE_DU_ZEPHYR);
        trésors[2] = new Tresor(NomTresor.LE_CRISTAL_ARDENT);
        trésors[3] = new Tresor(NomTresor.LE_CALICE_DE_L_ONDE);
        if (Parameters.LOGS) {
            System.out.println("\033[32mTrésors initialisés\033[0m");
        }
    }

    private boolean estTerminé() {
        return (estPerdu() || !jeuEnCours);
    }
    private boolean estPerdu() {
        Grille g = getGrille();
        Utils.EtatTuile coulee = Utils.EtatTuile.COULEE;
        int joueurVivant = getNbJoueur();
        for (Aventurier a : getJoueurs()) {
            if (a.getTuile().getEtat() == Utils.EtatTuile.COULEE) {
                joueurVivant--;
            }
        }
        return (g.getTuile(NomTuile.HELIPORT).getEtat() == coulee
                || getNiveau() >= 10
                || (g.getTuile(NomTuile.LE_TEMPLE_DU_SOLEIL).getEtat() == coulee && g.getTuile(NomTuile.LE_TEMPLE_DE_LA_LUNE).getEtat() == coulee && !getTrésors()[0].isGagne())
                || (g.getTuile(NomTuile.LE_JARDIN_DES_HURLEMENTS).getEtat() == coulee && g.getTuile(NomTuile.LE_JARDIN_DES_MURMURES).getEtat() == coulee && !getTrésors()[1].isGagne())
                || (g.getTuile(NomTuile.LA_CAVERNE_DES_OMBRES).getEtat() == coulee && g.getTuile(NomTuile.LA_CAVERNE_DU_BRASIER).getEtat() == coulee && !getTrésors()[2].isGagne())
                || (g.getTuile(NomTuile.LE_PALAIS_DE_CORAIL).getEtat() == coulee && g.getTuile(NomTuile.LE_PALAIS_DES_MAREES).getEtat() == coulee && !getTrésors()[3].isGagne())
                || joueurVivant < nbJoueurs);
    }
    
    private void debutJeu() {
        //méthode qui :
        /*
            - démarre le jeu
            - tire les premieres cartes innondations
            - place les aventuriers
            - distribue les cartes Trésor
         */

        for (Aventurier a : getNomJoueurs().keySet()) {
            getJoueurs().add(a);
        }
        this.pouvoirPiloteDispo = true;
        this.pouvoirIngénieurUsé = false;
        this.àPioché = false;
        this.jeuEnCours = true;
        Grille g = getGrille();
        //tout ceci dépendant du parametres.ALEAS
        if (!Parameters.ALEAS) {
            g.getTuile(3, 0).setEtat(Utils.EtatTuile.INONDEE);
            g.getTuile(1, 3).setEtat(Utils.EtatTuile.INONDEE);
            g.getTuile(3, 3).setEtat(Utils.EtatTuile.INONDEE);
            g.getTuile(3, 5).setEtat(Utils.EtatTuile.INONDEE);
            g.getTuile(5, 3).setEtat(Utils.EtatTuile.INONDEE);
            g.getTuile(2, 2).setEtat(Utils.EtatTuile.COULEE);
            g.getTuile(2, 3).setEtat(Utils.EtatTuile.COULEE);
            g.getTuile(2, 4).setEtat(Utils.EtatTuile.COULEE);
            g.getTuile(4, 3).setEtat(Utils.EtatTuile.COULEE);

            for (Aventurier a : getJoueurs()) {
                if (a.getCouleur() == Pion.BLANC) {
                    g.getTuile(1, 2).addAventurier(a);
                } else if (a.getCouleur() == Pion.BLEU) {
                    g.getTuile(3, 2).addAventurier(a);
                } else if (a.getCouleur() == Pion.JAUNE) {
                    g.getTuile(3, 1).addAventurier(a);
                } else if (a.getCouleur() == Pion.NOIR) {
                    g.getTuile(2, 1).addAventurier(a);
                } else if (a.getCouleur() == Pion.ROUGE) {
                    g.getTuile(3, 0).addAventurier(a);
                } else if (a.getCouleur() == Pion.VERT) {
                    g.getTuile(4, 2).addAventurier(a);
                }
                this.joueurCourant = a;
                gererCarteOrange();
            }
        } else { // ALEAS == true
            gererCarteBleue();
            for (Aventurier a : getJoueurs()) {
                boolean randomCorrect = false;
                do {
                    int x = (int) (Math.random() * 5);
                    int y = (int) (Math.random() * 5);
                    if (g.getTuile(x, y) != null) {
                        g.getTuile(x, y).addAventurier(a);
                        this.joueurCourant = a;
                        gererCarteOrange();
                        randomCorrect = true;
                    }
                } while (!randomCorrect);
            }
        }
        this.joueurCourant = getJoueurs().get(0);
        getIHM().dessinCartes(getJoueurCourant().getMain());
        getIHM().dessinCarteAventurier(getJoueurCourant());
        getIHM().actualiserNiveauEau(getNiveau());
        if (getJoueurCourant().getCouleur() == Pion.JAUNE) {
            this.nbActions = 4;
        } else {
            this.nbActions = 3;
        }
        if (Parameters.LOGS) {
            System.out.println("\033[31mLe jeu commence...\033[0m");
        }
    }
    private void actualiserJeu() {
        getIHM().actualiserNiveauEau(getNiveau());
        getGrille().deselectionnerTuiles();
        boolean mainDuJoueurPleine = (getJoueurCourant().getMain().size() > 5);
        this.pouvoirIngénieurUsé = false;
        if (this.estTerminé() && !sauvetageEnCours) {
            if (this.estPerdu()) {
                //partie perdue (a afficher sur l'ihm)
                if (Parameters.LOGS) {
                    System.out.println("\033[31mPartie perdue... Fin du jeu\033[0m");
                }
                int joueurVivant = getNbJoueur();
                for (Aventurier a : getJoueurs()) {
                    if (a.getTuile().getEtat()==Utils.EtatTuile.COULEE) {
                        joueurVivant--;
                    }
                }
                if (grille.getTuile(NomTuile.HELIPORT).getEtat() == Utils.EtatTuile.COULEE) {
                    getIHM().etatFin(2);
                } else if (getNiveau() >= 10) {
                    getIHM().etatFin(0);
                } else if (joueurVivant < getNbJoueur()) {
                    getIHM().etatFin(1);
                } else {
                    getIHM().etatFin(3);
                }
            } else {
                getIHM().etatFin(4);
                if (Parameters.LOGS) {
                    System.out.println("\033[33mPartie gagnée !\033[0m");
                }
            }
        } else {
            if (getNbAction() == 0) {
                if (!this.àPioché) {
                    mainDuJoueurPleine = gererCarteOrange();
                }
                if (mainDuJoueurPleine) {
                    this.àPioché = true;
                    getIHM().dessinCartes(getJoueurCourant().getMain());
                    getIHM().afficherEtatAction(ihm.ETAT_TROP_CARTES, getNomJoueurs().get(getJoueurCourant()), null);
                    getIHM().selectionCarte();
                } else {
                    gererCarteBleue();
                    pouvoirPiloteDispo = true;
                    int indexJNext;
                    if (getJoueurs().indexOf(getJoueurCourant()) + 1 >= getNbJoueur()) {
                        indexJNext = 0;
                    } else {
                        indexJNext = getJoueurs().indexOf(getJoueurCourant()) + 1;
                    }
                    this.joueurCourant = getJoueurs().get(indexJNext);
                    if (getJoueurCourant().getCouleur() == Pion.JAUNE) {
                        this.nbActions = 4;
                    } else {
                        this.nbActions = 3;
                    }
                    getIHM().dessinCarteAventurier(getJoueurCourant());
                    this.àPioché = false;
                    if (sauvetageEnCours) {
                        gererSauvetage();
                    } else {
                        if (getJoueurCourant().getMain().size() <= 5) {
                            getIHM().afficherEtatAction(ihm.ETAT_JOUEUR, getNomJoueurs().get(getJoueurCourant()), getNbAction());
                            getGrille().deselectionnerTuiles();
                            getIHM().interfaceParDefaut(getJoueurCourant().getMain());
                        } else {
                            getIHM().afficherEtatAction(ihm.ETAT_TROP_CARTES, getNomJoueurs().get(getJoueurCourant()), null);
                            getIHM().dessinCartes(getJoueurCourant().getMain());
                            getIHM().selectionCarte();
                        }
                    }
                }
            } else {
                if (mainDuJoueurPleine) {
                    getIHM().afficherEtatAction(ihm.ETAT_TROP_CARTES, getNomJoueurs().get(getJoueurCourant()), null);
                    getIHM().dessinCartes(getJoueurCourant().getMain());
                    getIHM().selectionCarte();
                } else {
                    getIHM().afficherEtatAction(ihm.ETAT_JOUEUR, getNomJoueurs().get(getJoueurCourant()), getNbAction());
                    getGrille().deselectionnerTuiles();
                    getIHM().dessinCarteAventurier(getJoueurCourant());
                    getIHM().interfaceParDefaut(getJoueurCourant().getMain());
                }
            }
        }
    }
    
    //METHODES UTILES
    private void voirJoueur(Aventurier joueurAffiché) {
        Aventurier joueurSuivant;
        if (getJoueurs().indexOf(joueurAffiché) + 1 >= getNbJoueur()) {
            joueurSuivant = getJoueurs().get(0);
        } else {
            joueurSuivant = getJoueurs().get(getJoueurs().indexOf(joueurAffiché) + 1);
        }
        if (joueurSuivant == getJoueurCourant()) {
            actualiserJeu();
        } else {
            getGrille().deselectionnerTuiles();
            getIHM().afficherJoueur(joueurSuivant.getMain(), joueurSuivant);
            getIHM().afficherEtatAction(ihm.ETAT_VOIR_JOUEUR, getNomJoueurs().get(joueurSuivant), null);
        }
    }
    private int getNbAction() {
        return this.nbActions;
    }
    private VueAventurier getIHM() {
        return this.ihm;
    }
    private Aventurier getJoueurCourant() {
        return this.joueurCourant;
    }
    private Tresor[] getTrésors() {
        return trésors;
    }
    private int getNbJoueur() {
        return this.nbJoueurs;
    }
    private Grille getGrille() {
        return grille;
    }
    private int getNiveau() {
        return niveauEau;
    }
    private ArrayList<Tuile> calculTouteTuileDispo() {
        Grille g = getGrille();
        ArrayList<Tuile> touteTuileDispo = new ArrayList<Tuile>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (g.getTuile(i, j) != null) {
                    if (g.getTuile(i, j).getEtat() != Utils.EtatTuile.COULEE) {
                        touteTuileDispo.add(g.getTuile(i, j));
                    }
                }
            }
        }
        return touteTuileDispo;
    }
    private ArrayList<Tuile> calculTouteTuileInnon() {
        Grille g = getGrille();
        ArrayList<Tuile> touteTuileInnon = new ArrayList<Tuile>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (g.getTuile(i, j) != null) {
                    if (g.getTuile(i, j).getEtat() == Utils.EtatTuile.INONDEE) {
                        touteTuileInnon.add(g.getTuile(i, j));
                    }
                }
            }
        }
        return touteTuileInnon;
    }
    private HashMap<Aventurier, String> getNomJoueurs() {
        return nomJoueurs;
    }
    private ArrayList<Aventurier> getJoueurs() {
        return this.joueurs;
    }

    //méthodes pour les cartes bleues
    //pioche
    private Stack<CarteBleue> getPiocheBleues() {
        return piocheBleues;
    }
    private CarteBleue piocheCarteBleue() {
        return this.piocheBleues.pop();
    }
    private void addPiocheBleue(CarteBleue c) {
        this.piocheBleues.push(c);
    }
    //défausse
    private ArrayList<CarteBleue> getDefausseBleues() {
        return this.defausseBleues;
    }
    private void addDefausseBleues(CarteBleue carte) {
        this.defausseBleues.add(carte);
        getIHM().actualiserDefausseB(carte);
    }
    private void viderDefausseBleues() {
        this.defausseBleues.removeAll(this.defausseBleues);
        getIHM().actualiserDefausseB(null);
    }

    //méthodes pour les cartes oranges
    //pioche
    private Stack<CarteOrange> getPiocheOranges() {
        return piocheOranges;
    }
    private CarteOrange piocheCarteOrange() {
        return this.piocheOranges.pop();
    }
    private void addPiocheOrange(CarteOrange c) {
        this.piocheOranges.push(c);
    }

    //défausse
    private ArrayList<CarteOrange> getDefausseOranges() {
        return this.defausseOranges;
    }
    private void addDefausseOranges(CarteOrange carte) {
        this.defausseOranges.add(carte);
        getIHM().actualiserDefausseO(carte);
    }
    private void viderDefausseOranges() {
        this.defausseOranges.removeAll(this.defausseOranges);
        getIHM().actualiserDefausseO(null);
    }

    //CONSTUCTEUR
    public Controleur() {
        accueil = new VueAccueil();
        accueil.addObservateur(this);
    }

    //MAIN
    public static void main(String[] args) {
        new Controleur();
    }
}
