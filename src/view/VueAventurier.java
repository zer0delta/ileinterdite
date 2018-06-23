/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import ileinterdite.Message;
import ileinterdite.Observe;
import ileinterdite.TypesMessages;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static javax.swing.SwingConstants.CENTER;
import model.Aventurier;
import model.CarteOrange;
import model.CarteTrésor;
import model.Grille;
import model.Tresor;

/**
 *
 * @author yannic
 */
public class VueAventurier extends Observe {

    public static final int ETAT_COMMENCER = 1;
    public static final int ETAT_SOUHAITE_DEPLACEMENT = 2;
    public static final int ETAT_SOUHAITE_ASSECHER = 3;
    public static final int ETAT_SOUHAITE_DONNER = 4;
    public static final int ETAT_GAGNER_TRESOR = 5;
    public static final int ETAT_TROP_CARTES = 6;
    public static final int ETAT_SOUHAITE_JOUER_SPECIALE = 7;
    public static final int ETAT_JOUEUR = 8;

    private TypesMessages MESSAGE_PRECEDENT;

    private JFrame window;
    private JButton bDepl, bAss, bPioch, bGagner, bSpecial, bAnnuler, bFinir, bPerso;
    private JLabel instructions;
    private ImageFond imageFond;

    private ImagePanel tresor1, tresor2, tresor3, tresor4, niveauEau;
    private ImagePanel carte1, carte2, carte3, carte4, carte5, carte6, carte7, carte8, carte9;
    private ArrayList<ImagePanel> lesCartes;
    private ImagePanel cartesOranges, cartesBleues, defausseO, defausseB, perso;
    private Grille grille;
    private VidePanel margeHaut;

    private final int lfenetre = 1280;
    private final int hfenetre = 720;

    public VueAventurier(Grille grille) {
        
        // INSTANCIATION DE L'IMAGE DE FOND
        imageFond = new ImageFond(lfenetre, hfenetre, "/src/images/autre/fondJeu.jpg");

        // INSTANCIATIONS DES ÉLÉMENTS DE L'IHM
        window = new JFrame();

        bDepl = new JButton("Déplacer");
        bAss = new JButton("Assécher");
        bPioch = new JButton("Donner une carte");
        bGagner = new JButton("Gagner un trésor");
        bSpecial = new JButton("Jouer une carte spéciale");
        bAnnuler = new JButton("Annuler");
        bFinir = new JButton("Finir de jouer");
        bPerso = new JButton("Autres joueurs");
        bPerso.setPreferredSize(new Dimension(87, 130));
        bPerso.setText("<html><center>" + "Autres" + "<br>" + "joueurs" + "</center></html>");

        instructions = new JLabel();
        instructions.setForeground(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 15);
        instructions.setFont(font);

        tresor1 = new ImagePanel(60, 90, System.getProperty("user.dir") + "/src/images/tresors/calice.png");
        tresor2 = new ImagePanel(60, 90, System.getProperty("user.dir") + "/src/images/tresors/cristal.png");;
        tresor3 = new ImagePanel(60, 90, System.getProperty("user.dir") + "/src/images/tresors/pierre.png");
        tresor4 = new ImagePanel(60, 90, System.getProperty("user.dir") + "/src/images/tresors/zephyr.png");
        niveauEau = new ImagePanel(130, 395, System.getProperty("user.dir") + "/src/images/autre/Niveau.png");

        carte1 = new ImagePanel(67, 100, "", 14);
        carte2 = new ImagePanel(67, 100, "", 14);
        carte3 = new ImagePanel(67, 100, "", 14);
        carte4 = new ImagePanel(67, 100, "", 14);
        carte5 = new ImagePanel(67, 100, "", 14);
        carte6 = new ImagePanel(67, 100, "", 14);
        carte7 = new ImagePanel(67, 100, "", 14);
        carte8 = new ImagePanel(67, 100, "", 14);
        carte9 = new ImagePanel(67, 100, "", 14);

        lesCartes = new ArrayList<>();
        lesCartes.add(carte1);
        lesCartes.add(carte2);
        lesCartes.add(carte3);
        lesCartes.add(carte4);
        lesCartes.add(carte5);
        lesCartes.add(carte6);
        lesCartes.add(carte7);
        lesCartes.add(carte8);
        lesCartes.add(carte9);

        cartesOranges = new ImagePanel(87, 130, System.getProperty("user.dir") + "/src/images/cartes/Fond rouge.png", 6);
        cartesBleues = new ImagePanel(87, 130, System.getProperty("user.dir") + "/src/images/cartes/Fond bleu.png", 6);
        defausseO = new ImagePanel(87, 130, System.getProperty("user.dir") + "/src/images/cartes/Fond rouge.png", 6);
        defausseB = new ImagePanel(87, 130, System.getProperty("user.dir") + "/src/images/cartes/Fond bleu.png", 6);
        perso = new ImagePanel(87, 130, "", 6);

        this.MESSAGE_PRECEDENT = null;

        this.grille = grille;
        this.grille.setPreferredSize(new Dimension(500, 500));
        this.grille.setBackground(new Color(50, 50, 230));

        margeHaut = new VidePanel(400, 25);

        // INSTANCIATION DES JPANEL DE DISPOSITION      
        JPanel layer0 = new JPanel();
        
        JPanel layer1north = new JPanel();
        JPanel layer1south = new JPanel();

        JPanel layer2north = new JPanel();
        JPanel layer2south = new JPanel();
        JPanel layer2east = new JPanel();
        JPanel layer2west = new JPanel();

        JPanel layer3west = new JPanel();
        JPanel layer3east = new JPanel();

        JPanel layer4center = new JPanel();
        JPanel layer4north = new JPanel();

        // AFFECTATION DE TYPES AUX PANELS DE DISPOSITION
        layer0.setLayout(new BorderLayout());
        layer0.setBackground(Color.RED);
        
        layer1north.setLayout(new BorderLayout());
        layer1south.setLayout(new BorderLayout());
        
        layer2north.setLayout(new BorderLayout());
        layer2south.setLayout(new GridLayout(1, 9));
        layer2east.setLayout(new BorderLayout());
        layer2west.setLayout(new BorderLayout());

        layer3west.setLayout(new GridLayout(3, 2));
        layer3west.setPreferredSize(new Dimension(200, 300));
        layer3east.setLayout(new BorderLayout());

        layer4center.setLayout(new GridLayout(7, 1));
        layer4north.setLayout(new GridLayout(1, 4));

        // PLACEMENT DES PANELS DE DISPOSITION
        layer1north.add(layer2north, BorderLayout.NORTH);
        layer1north.add(layer2east, BorderLayout.EAST);
        layer1north.add(layer2west, BorderLayout.WEST);

        layer1south.add(layer2south, BorderLayout.CENTER);

        layer2west.add(layer3west, BorderLayout.CENTER);
        layer2east.add(layer3east, BorderLayout.CENTER);

        layer3east.add(layer4center, BorderLayout.CENTER);
        layer3east.add(layer4north, BorderLayout.NORTH);

        layer0.add(layer1north, BorderLayout.NORTH);
        layer0.add(layer1south, BorderLayout.SOUTH);

        // AJOUTS DES MARGES
        layer1south.add(new VidePanel(200, 35), BorderLayout.NORTH);
        layer1south.add(new VidePanel(190, 1), BorderLayout.EAST);
        layer1south.add(new VidePanel(190, 1), BorderLayout.WEST);
        layer1south.add(new VidePanel(1, 20), BorderLayout.SOUTH);
        
        layer2north.add(margeHaut, BorderLayout.NORTH);
        layer2north.add(new VidePanel(400, 0), BorderLayout.EAST);
        layer2north.add(new VidePanel(400, 0), BorderLayout.WEST);
        layer2north.add(new VidePanel(400, 10), BorderLayout.SOUTH);

        layer2east.add(new VidePanel(300, 15), BorderLayout.NORTH);
        layer2east.add(new VidePanel(80, 400), BorderLayout.EAST);
        layer2east.add(new VidePanel(60, 400), BorderLayout.WEST);
        layer2east.add(new VidePanel(300, 15), BorderLayout.SOUTH);

        layer2west.add(new VidePanel(300, 60), BorderLayout.NORTH);
        layer2west.add(new VidePanel(60, 400), BorderLayout.EAST);
        layer2west.add(new VidePanel(80, 400), BorderLayout.WEST);
        layer2west.add(new VidePanel(300, 60), BorderLayout.SOUTH);

        // AJOUT DES ÉLÉMENTS AUX PANELS DE DISPOSITION
        layer1north.add(grille, BorderLayout.CENTER);

        layer2north.add(instructions, BorderLayout.CENTER);

        layer2south.add(carte1);
        layer2south.add(carte2);
        layer2south.add(carte3);
        layer2south.add(carte4);
        layer2south.add(carte5);
        layer2south.add(carte6);
        layer2south.add(carte7);
        layer2south.add(carte8);
        layer2south.add(carte9);

        layer3east.add(niveauEau, BorderLayout.WEST);

        layer3west.add(defausseO);
        layer3west.add(cartesOranges);
        layer3west.add(defausseB);
        layer3west.add(cartesBleues);
        layer3west.add(perso);
        layer3west.add(bPerso);

        layer4center.add(bDepl);
        layer4center.add(bAss);
        layer4center.add(bPioch);
        layer4center.add(bGagner);
        layer4center.add(bSpecial);
        layer4center.add(bAnnuler);
        layer4center.add(bFinir);

        layer4north.add(tresor1);

        layer4north.add(tresor2);
        layer4north.add(tresor3);
        layer4north.add(tresor4);

        window.add(imageFond);
        imageFond.add(layer0);
        
        tresor1.setVisible(false);
        tresor2.setVisible(false);
        tresor3.setVisible(false);
        tresor4.setVisible(false);
        
        // MISE EN TRANSPARENCE DE TOUS LES PANELS POUR VOIR L'ARRIÈRE PLAN
        layer0.setOpaque(false);
        layer1north.setOpaque(false);
        layer1south.setOpaque(false);
        layer2north.setOpaque(false);
        layer2south.setBackground(new Color(255, 229, 204, 100));
            carte1.setBackground(new Color(255, 229, 204));
            carte2.setBackground(new Color(255, 229, 204));
            carte3.setBackground(new Color(255, 229, 204));
            carte4.setBackground(new Color(255, 229, 204));
            carte5.setBackground(new Color(255, 229, 204));
            carte6.setBackground(new Color(255, 229, 204));
            carte7.setBackground(new Color(255, 229, 204));
            carte8.setBackground(new Color(255, 229, 204));
            carte9.setBackground(new Color(255, 229, 204));
            
        layer2east.setOpaque(false);
        layer2west.setOpaque(false);
        layer3west.setBackground(new Color(255, 242, 230, 100));
        layer3east.setOpaque(false);
        layer4center.setOpaque(false);
        layer4north.setBackground(new Color(255, 229, 204, 100));

        // ACTIONLISTENER DES BOUTONS
        bDepl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message();
                m.type = TypesMessages.SOUHAITE_DEPLACEMENT;
                MESSAGE_PRECEDENT = m.type;

                notifierObservateur(m);
            }
        });
        bAnnuler.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message();
                m.type = TypesMessages.ANNULER;
                MESSAGE_PRECEDENT = m.type;

                notifierObservateur(m);
            }
        });
        bAss.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message();
                m.type = TypesMessages.SOUHAITE_ASSECHER;
                MESSAGE_PRECEDENT = m.type;

                notifierObservateur(m);
            }
        });
        bFinir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message();
                m.type = TypesMessages.FINIR_TOUR;
                MESSAGE_PRECEDENT = m.type;

                notifierObservateur(m);
            }
        });

        bGagner.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message();
                m.type = TypesMessages.ACTION_GAGNER_TRESOR;
                MESSAGE_PRECEDENT = m.type;

                notifierObservateur(m);
            }
        });

        bPioch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message();
                m.type = TypesMessages.SOUHAITE_DONNER;
                MESSAGE_PRECEDENT = m.type;

                notifierObservateur(m);
            }
        });
        bSpecial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message();
                m.type = TypesMessages.SOUHAITE_JOUER_SPECIALE;
                MESSAGE_PRECEDENT = m.type;

                notifierObservateur(m);
            }
        });

        // CLIC SUR LA GRILLE
        this.grille.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                switch (MESSAGE_PRECEDENT) {
                    case SOUHAITE_DEPLACEMENT:
                        m.type = TypesMessages.ACTION_DEPLACEMENT;
                        break;
                    case SOUHAITE_ASSECHER:
                    case ACTION_ASSECHER:
                        m.type = TypesMessages.ACTION_ASSECHER;
                        break;
                    /*case SOUHAITE_DONNER:
                        m.type = TypesMessages.ACTION_DONNER;
                        
                        break;*/
                    case SOUHAITE_JOUER_SPECIALE:
                        m.type = TypesMessages.JOUER_SPECIALE;
                        break;
                }
                if (grille.getTuile(e.getX() * 6 / grille.getWidth(), e.getY() * 6 / grille.getHeight()).getSelected() != 0) {
                    m.tuile = grille.getTuile(e.getX() * 6 / grille.getWidth(), e.getY() * 6 / grille.getHeight());
                    MESSAGE_PRECEDENT = m.type;
                    notifierObservateur(m);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // CLIC SUR LES CARTES
        this.carte1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 0;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.carte2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 1;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.carte3.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 2;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.carte4.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 3;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.carte5.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 4;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.carte6.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 5;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.carte7.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 6;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.carte8.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 7;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        this.carte9.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Message m = new Message();
                m.type = TypesMessages.JOUER_SPECIALE;
                m.numCarte = 8;
                notifierObservateur(m);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        
        // PROPRIÉTÉS DU JFRAME
        window.setTitle("L'Île Interdite");
        window.setSize(lfenetre, hfenetre); // équivalent 16:9
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
        window.setResizable(false);
    }

    public void dessinCartes(ArrayList<CarteOrange> cartes) {

        ArrayList<CarteTrésor> cartesTresors = new ArrayList<CarteTrésor>();
        for (CarteOrange c : cartes) {
            if (c.getRole().equals("Trésor")) {
                cartesTresors.add((CarteTrésor) c);
            } else {
                cartesTresors.add(null);
            }
        }

        for (int i = 0; i < cartes.size(); i++) {
            lesCartes.get(i).setImage(System.getProperty("user.dir") + "/src/images/cartes/" + cartes.get(i).getRole() + ((cartes.get(i).getRole() == "Trésor") ? cartesTresors.get(i).getNomTresor() : "") + ".png");
        }
    }

    public void afficherEtatAction(int etat, String joueur, Integer nbaction) {
        switch (etat) {
            case ETAT_COMMENCER:
                margeHaut.setPreferredSize(new Dimension(400, 9));
                instructions.setText("Bienvenue " + joueur + ". Il vous reste " + nbaction + " actions");
                break;
            case ETAT_SOUHAITE_DEPLACEMENT:
                instructions.setText("Choissisez une tuile :");
                break;

            case ETAT_SOUHAITE_ASSECHER:
                instructions.setText("Choissisez une tuile :");
                break;

            case ETAT_SOUHAITE_DONNER:
                instructions.setText("Choissisez une carte à donner :");
                break;

            case ETAT_TROP_CARTES:
                margeHaut.setPreferredSize(new Dimension(400, 9));
                instructions.setText("Vous avez trop de cartes dans votre main, vous devez en défausser une");
                break;

            case ETAT_SOUHAITE_JOUER_SPECIALE:
                instructions.setText("Choissisez une carte :");
                break;

            case ETAT_JOUEUR:
                margeHaut.setPreferredSize(new Dimension(400, 9));
                instructions.setText("Joueur " + joueur + " c'est à vous de jouer. Il vous reste " + nbaction + ((nbaction < 2) ? " action" : " actions"));
                break;

        }

    }
    
    public void dessinCarteAventurier(Aventurier joueur) {
        perso.setImage(System.getProperty("user.dir") + "/src/images/personnages/" + joueur.getClass().toString().substring(12).toLowerCase() + ".png");
    }

    public void interfaceParDefaut(ArrayList<CarteOrange> cartes) {
        grille.repaint();
        dessinCartes(cartes);
        bDepl.setEnabled(true);
        bAss.setEnabled(true);
        bPioch.setEnabled(true);
        bGagner.setEnabled(true);
        bSpecial.setEnabled(true);
        bAnnuler.setEnabled(true);
        bFinir.setEnabled(true);
        bPerso.setEnabled(true);
    }

    public void afficheCartesHelico(ArrayList<CarteOrange> cartes) {
        bDepl.setEnabled(false);
        bAss.setEnabled(false);
        bPioch.setEnabled(false);
        bGagner.setEnabled(false);
        bSpecial.setEnabled(false);
        for (CarteOrange c : cartes) {
            if (c.getRole().equals("Helicoptere")) {
                lesCartes.get(cartes.indexOf(c)).setImage(System.getProperty("user.dir") + "/src/images/cartes/" + cartes.get(cartes.indexOf(c)).getRole() + "Dispo.png");
            }
        }
    }

    public void afficheCartesSac(ArrayList<CarteOrange> cartes) {
        this.grille.repaint();
        bDepl.setEnabled(false);
        bAss.setEnabled(false);
        bPioch.setEnabled(false);
        bGagner.setEnabled(false);
        bSpecial.setEnabled(false);
        for (CarteOrange c : cartes) {
            if (c.getRole().equals("Sac de sable")) {
                lesCartes.get(cartes.indexOf(c)).setImage(System.getProperty("user.dir") + "/src/images/cartes/" + cartes.get(cartes.indexOf(c)).getRole() + "Dispo.png");
            }
        }
    }

    public void afficherTuilesDispo() {
        this.grille.repaint();
        bDepl.setEnabled(false);
        bAss.setEnabled(false);
        bPioch.setEnabled(false);
        bGagner.setEnabled(false);
        bSpecial.setEnabled(false);
    }

    public void afficherTuilesPilote() {
        this.grille.repaint();
    }

    public void actualiserTrésor(Tresor[] trésors) {
        if (trésors[1].isGagne()) {
            tresor1.setVisible(true);
        } else if (trésors[2].isGagne()) {
            tresor2.setVisible(true);
        } else if (trésors[3].isGagne()) {
            tresor3.setVisible(true);
        } else if (trésors[4].isGagne()) {
            tresor4.setVisible(true);
        }
    }
    
    public void majNiveauEau(int nivo){
        niveauEau.setImage(System.getProperty("user.dir") + "/src/images/autre/n"+nivo+".png");
    }

}
