/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author yannic
 */
public class IHM extends JFrame {
    
    private JButton bDepl, bAss, bPioch, bGagner;
    private JLabel instructions;
/*    
    private ImagePanel tresor1, tresor2, tresor3, tresor4, niveauEau;
    private ImagePanel carte1, carte2, carte3, carte4, carte5, carte6, carte7, carte8, carte9;
    private ImagePanel cartesOranges, cartesBleues, defausseO, defausseB;
*/
    private JPanel tresor1, tresor2, tresor3, tresor4, niveauEau;
    private JPanel carte1, carte2, carte3, carte4, carte5, carte6, carte7, carte8, carte9;
    private JPanel cartesOranges, cartesBleues, defausseO, defausseB;
    private JPanel grille;
    
    private final int lfenetre = 1280;
    private final int hfenetre = 720;
    
    public IHM() {

        // INSTANCIATIONS DES ÉLÉMENTS DE L'IHM
        bDepl = new JButton("Déplacer");
        bAss = new JButton("Assécher");
        bPioch = new JButton("Piocher");
        bGagner = new JButton("Gagner un trésor");
        
        instructions = new JLabel("Instructions");
/*       
        tresor1 = new ImagePanel();
        tresor2 = new ImagePanel();
        tresor3 = new ImagePanel();
        tresor4 = new ImagePanel();
        niveauEau = new ImagePanel();
        
        carte1 = new ImagePanel();
        carte2 = new ImagePanel();
        carte3 = new ImagePanel();
        carte4 = new ImagePanel();
        carte5 = new ImagePanel();
        carte6 = new ImagePanel();
        carte7 = new ImagePanel();
        carte8 = new ImagePanel();
        carte9 = new ImagePanel();
        
        cartesOranges = new ImagePanel();
        cartesBleues = new ImagePanel();
        defausseO = new ImagePanel();
        defausseB = new ImagePanel();
*/        

        tresor1 = new JPanel();
        tresor1.setPreferredSize(new Dimension(60,90));
        tresor1.setBackground(Color.red);
        
        tresor2 = new JPanel();
        tresor2.setPreferredSize(new Dimension(60,90));
        tresor2.setBackground(Color.red);
        
        tresor3 = new JPanel();
        tresor3.setPreferredSize(new Dimension(60,90));
        tresor3.setBackground(Color.red);
        
        tresor4 = new JPanel();
        tresor4.setPreferredSize(new Dimension(60,90));
        tresor4.setBackground(Color.red);
        
        niveauEau = new JPanel();
        niveauEau.setPreferredSize(new Dimension(70,240));
        niveauEau.setBackground(Color.blue);
         
        carte1 = new JPanel();
        carte1.setPreferredSize(new Dimension(100,150));
        carte1.setBackground(Color.orange);
        carte2 = new JPanel();
        carte2.setPreferredSize(new Dimension(100,150));
        carte2.setBackground(Color.orange);
        carte3 = new JPanel();
        carte3.setPreferredSize(new Dimension(100,150));
        carte3.setBackground(Color.orange);
        carte4 = new JPanel();
        carte4.setPreferredSize(new Dimension(100,150));
        carte4.setBackground(Color.orange);
        carte5 = new JPanel();
        carte5.setPreferredSize(new Dimension(100,150));
        carte5.setBackground(Color.orange);
        carte6 = new JPanel();
        carte6.setPreferredSize(new Dimension(100,150));
        carte6.setBackground(Color.orange);
        carte7 = new JPanel();
        carte7.setPreferredSize(new Dimension(100,150));
        carte7.setBackground(Color.orange);
        carte8 = new JPanel();
        carte8.setPreferredSize(new Dimension(100,150));
        carte8.setBackground(Color.orange);
        carte9 = new JPanel();
        carte9.setPreferredSize(new Dimension(100,150));
        carte9.setBackground(Color.orange);
        
        cartesOranges = new JPanel();
        cartesOranges.setPreferredSize(new Dimension(100,150));
        cartesOranges.setBackground(Color.orange);
        cartesBleues = new JPanel();
        cartesBleues.setPreferredSize(new Dimension(100,150));
        cartesBleues.setBackground(Color.blue);
        defausseO = new JPanel();
        defausseO.setPreferredSize(new Dimension(100,150));
        defausseO.setBackground(Color.gray);
        defausseB = new JPanel();
        defausseB.setPreferredSize(new Dimension(100,150));
        defausseB.setBackground(Color.gray);
        
        grille = new JPanel();
        grille.setPreferredSize(new Dimension(400,400));
        grille.setBackground(Color.black);

        // INSTANCIATION DES JPANEL DE DISPOSITION
        JPanel layer0 = new JPanel();

        JPanel layer1north = new JPanel();
        JPanel layer1south = new JPanel();
        
        JPanel layer2north = new JPanel();
        JPanel layer2south = new JPanel();
        JPanel layer2east = new JPanel();
        JPanel layer2west = new JPanel();

        JPanel layer3westCenter = new JPanel();
        JPanel layer3eastCenter = new JPanel();

        JPanel layer4eastCenterCenter = new JPanel();
        JPanel layer4eastCenterNorth = new JPanel();

        // AFFECTATION DE TYPES AUX PANELS
        layer0.setLayout(new BorderLayout());

        layer1north.setLayout(new BorderLayout());
        layer1south.setLayout(new BorderLayout());

        layer2north.setLayout(new BorderLayout());
        layer2south.setLayout(new GridLayout(1, 9));
        layer2east.setLayout(new BorderLayout());
        layer2west.setLayout(new BorderLayout());

        layer3westCenter.setLayout(new GridLayout(2, 2));
        layer3eastCenter.setLayout(new BorderLayout());

        layer4eastCenterCenter.setLayout(new GridLayout(4, 1));
        layer4eastCenterNorth.setLayout(new GridLayout(1, 4));

        // PLACEMENT DES PANELS
        layer1north.add(layer2north, BorderLayout.NORTH);
        layer1north.add(layer2east, BorderLayout.EAST);
        layer1north.add(layer2west, BorderLayout.WEST);
        layer1south.add(layer2south, BorderLayout.CENTER);
        
        layer2west.add(layer3westCenter, BorderLayout.CENTER);
        layer2east.add(layer3eastCenter, BorderLayout.CENTER);
        
        layer3eastCenter.add(layer4eastCenterCenter, BorderLayout.CENTER);
        layer3eastCenter.add(layer4eastCenterNorth, BorderLayout.NORTH);
        
        layer0.add(layer1north, BorderLayout.NORTH);
        layer0.add(layer1south, BorderLayout.SOUTH);
        
        // AJOUTS DES MARGES
        layer1south.add(new VidePanel(35, 900), BorderLayout.NORTH);
        layer1south.add(new VidePanel(190, 900), BorderLayout.EAST);
        layer1south.add(new VidePanel(190, 900), BorderLayout.WEST);
        layer1south.add(new VidePanel(40, 900), BorderLayout.SOUTH);
        
        layer2north.add(new VidePanel(400, 40), BorderLayout.NORTH);
        layer2north.add(new VidePanel(440, 95), BorderLayout.EAST);
        layer2north.add(new VidePanel(440, 65), BorderLayout.WEST);
        layer2north.add(new VidePanel(400, 25), BorderLayout.SOUTH);
        
        layer2east.add(new VidePanel(440, 15), BorderLayout.NORTH);
        layer2east.add(new VidePanel(120, 400), BorderLayout.EAST);
        layer2east.add(new VidePanel(80, 400), BorderLayout.WEST);
        layer2east.add(new VidePanel(440, 15), BorderLayout.SOUTH);
        
        layer2west.add(new VidePanel(440, 30), BorderLayout.NORTH);
        layer2west.add(new VidePanel(80, 400), BorderLayout.EAST);
        layer2west.add(new VidePanel(120, 400), BorderLayout.WEST);
        layer2west.add(new VidePanel(440, 30), BorderLayout.SOUTH);
        
        // PLACEMENT DES ÉLÉMENTS    
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
        
        layer3eastCenter.add(niveauEau, BorderLayout.WEST);
        
        layer3westCenter.add(defausseO);
        layer3westCenter.add(cartesOranges);
        layer3westCenter.add(defausseB);
        layer3westCenter.add(cartesBleues);
        
        layer4eastCenterCenter.add(bDepl);
        layer4eastCenterCenter.add(bAss);
        layer4eastCenterCenter.add(bPioch);
        layer4eastCenterCenter.add(bGagner);
        
        layer4eastCenterNorth.add(tresor1);
        layer4eastCenterNorth.add(tresor2);
        layer4eastCenterNorth.add(tresor3);
        layer4eastCenterNorth.add(tresor4);

        
        this.add(layer0);
        // PROPRIÉTÉS DU JFRAME
        this.setTitle("L'Île Interdite");
        this.setSize(lfenetre, hfenetre); // équivalent 16:9
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setResizable(false);
    }

}
