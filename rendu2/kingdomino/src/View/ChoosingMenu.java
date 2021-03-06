package View;

import Model.KingColor;
import Utilities.FontReader;
import Utilities.IMGReader;
import View.Components.MyLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Locale;

public class ChoosingMenu extends JPanel {
    private MyWindow mainFrame;
    private JPanel mainPnl = new JPanel();
    private ButtonGroup group;
    private String[] nbPlayerChoice = {"2","3","4"};
    JComboBox nbPlayerCbo = new JComboBox(nbPlayerChoice);


    //Couleur des boutons (orangé)
    private final Color btnColor = new Color(174,135,0);
    //Couleur des boutons en hover (orangé plus clair)
    private final Color btnHoverColor = new Color(198, 159, 22);

    //Color of combobox
    private final Color cboColor = new Color(255, 248, 147);



    /** Panel qui va contenir tout les cbo de couleurs (2 à 4) **/
    JPanel playerColorsPnl = new JPanel();

    private ImageIcon img;

    public ChoosingMenu(MyWindow MyWindow) {
        this.mainFrame = MyWindow;
        this.img = IMGReader.getImage("wallpaperDark.png");
        this.setOpaque(false);
        this.setLayout( new BorderLayout() );

        mainPnl.setLayout( new GridBagLayout() );
        mainPnl.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        this.add( mainPnl, BorderLayout.CENTER );
        int margin = 20;
        c.insets = new Insets(margin, margin, margin, margin);




                /***Affichage du titre de la page***/
        MyLabel label = new MyLabel("Menu de choix");
        label.setFont(FontReader.getInstance().getShowcard().deriveFont(Font.BOLD).deriveFont(80f));
        label.setOutlineColor(Color.DARK_GRAY);
        label.setStroke(new BasicStroke(5f));
        label.setForeground(Color.WHITE);
        label.setVisible(true);
        c.gridx = 0;
        c.gridy = 0;
        mainPnl.add( label, c );

                    /**Affichage du mode de jeu + des checkbox**/
        MyLabel mode = new MyLabel("Mode de jeu :");
        mode.setForeground(Color.WHITE);
        mode.setFont(FontReader.getInstance().getBookmanold().deriveFont(Font.BOLD).deriveFont(30f));
        mode.setOutlineColor(Color.DARK_GRAY);
        mode.setStroke(new BasicStroke(3f));
        c.gridx = 0;
        c.gridy = 1;
        mainPnl.add(mode, c);


        JCheckBox harmonyChk = new JCheckBox("Harmony");
        harmonyChk.setOpaque(false);
        harmonyChk.setForeground(Color.WHITE);
        harmonyChk.setFont(FontReader.getInstance().getBookmanold().deriveFont(Font.BOLD).deriveFont(35f));
        harmonyChk.setFocusPainted(false);
        harmonyChk.setIcon( IMGReader.getImage("unchecked_Checkbox.png") );
        harmonyChk.setSelectedIcon( IMGReader.getImage("checked_Checkbox.png") );
        harmonyChk.setRolloverIcon( IMGReader.getImage("hover_unchecked_Checkbox.png") );
        harmonyChk.setRolloverSelectedIcon( IMGReader.getImage("hover_checked_Checkbox.png") );
        harmonyChk.setMargin(new Insets(0, 5, 0, 55));

        JCheckBox middleKingdomChk = new JCheckBox("Middle Kingdom");
        middleKingdomChk.setOpaque(false);
        middleKingdomChk.setForeground(Color.WHITE);
        middleKingdomChk.setFont(FontReader.getInstance().getBookmanold().deriveFont(Font.BOLD).deriveFont(35f));
        middleKingdomChk.setFocusPainted(false);
        middleKingdomChk.setIcon( IMGReader.getImage("unchecked_Checkbox.png") );
        middleKingdomChk.setSelectedIcon( IMGReader.getImage("checked_Checkbox.png") );
        middleKingdomChk.setRolloverIcon( IMGReader.getImage("hover_unchecked_Checkbox.png") );
        middleKingdomChk.setRolloverSelectedIcon( IMGReader.getImage("hover_checked_Checkbox.png") );



        c.gridx = 1;
        c.gridy = 1;
        mainPnl.add(harmonyChk, c);
        c.gridx = 2;
        c.gridy = 1;
        mainPnl.add(middleKingdomChk, c);

                    /**Les combobox**/
        //ajout du label de combobox
        MyLabel nbPlayerLbl = new MyLabel("Nombre de joueurs :");
        nbPlayerLbl.setOutlineColor(Color.DARK_GRAY);
        nbPlayerLbl.setStroke(new BasicStroke(3f));
        nbPlayerLbl.setForeground(Color.WHITE);
        nbPlayerLbl.setFont(FontReader.getInstance().getBookmanold().deriveFont(Font.BOLD).deriveFont(30f));
        c.gridx = 0;
        c.gridy = 2;
        mainPnl.add(nbPlayerLbl, c);
        nbPlayerCbo.setPreferredSize(new Dimension(50,50));
        nbPlayerCbo.setFont(FontReader.getInstance().getAlgerian().deriveFont(Font.BOLD).deriveFont(30f));
        nbPlayerCbo.setBackground( cboColor );
        c.gridx = 1;
        c.gridy = 2;
        mainPnl.add(nbPlayerCbo, c);

        /**Affichage des cbo qui permet de choisir la couleur des rois des joueurs**/
        nbPlayerCbo.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
                createColorCbo();
            }
        });

        KingColor[] colors = KingColor.values();

        /** Panel de choix des couleurs **/
        playerColorsPnl.setLayout( new GridBagLayout() );
        //playerColorsPnl.setBackground( mycolor  );
        playerColorsPnl.setOpaque(false);
        createColorCbo();




        JButton playBtn = new JButton("COMMENCER");
        playBtn.setBackground(btnColor);
        playBtn.setBorder(BorderFactory.createLineBorder(Color.darkGray, 2));
        playBtn.setPreferredSize(new Dimension(250,60));
        playBtn.setFont(FontReader.getInstance().getAlgerian().deriveFont(Font.BOLD).deriveFont(30f));
        playBtn.setFocusPainted(false);
        //HOVER BUTTON
        playBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playBtn.setBackground(btnHoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                playBtn.setBackground(btnColor);
                //btnReverse.setBackground(UIManager.getColor("control"));
            }
        });
        playBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //Si les couleurs ne sont pas différentes
                if(!isDifferentColor()){
                    /*** Affichage du msg d'erreur ***/
                    MyLabel label = new MyLabel("Vous devez choisir des couleurs différentes pour chaque joueur !");
                    label.setFont(FontReader.getInstance().getBookmanold().deriveFont(Font.TRUETYPE_FONT).deriveFont(20f));
                    label.setOutlineColor(Color.DARK_GRAY);
                    label.setStroke(new BasicStroke(5f));
                    label.setForeground(new Color(255, 49, 49));
                    c.gridx = 0;
                    c.gridy = 6;
                    mainPnl.add( label, c );
                    mainPnl.revalidate();
                    mainPnl.repaint();
                    return;
                }

                /** Récupere toutes les couleurs choisi par les joueurs **/
                ArrayList<KingColor> colors = new ArrayList<>();
                ArrayList<String> pseudo = new ArrayList<>();
                for (Component component : playerColorsPnl.getComponents()) {
                   if (component instanceof JComboBox)
                   {
                        JComboBox cbo = (JComboBox)component;
                        colors.add( (KingColor) cbo.getSelectedItem() );
                   }
                   else {
                       JTextField name = (JTextField) component;
                       if (!name.getText().equals("name"))
                       {
                           pseudo.add(name.getText());
                       }
                       else
                       {
                           pseudo.add("");
                       }
                   }
                }

                int gameMode = 0;
                if(harmonyChk.isSelected() && middleKingdomChk.isSelected() ){
                    gameMode = 3;
                }else if( harmonyChk.isSelected() ){
                    gameMode = 1;
                }else if (middleKingdomChk.isSelected()){
                    gameMode = 2;
                }

                //Donne les infos de mode de jeu, de nombre de joueur et de couleurs des joueurs
                mainFrame.getGameController().initializeGame( Integer.parseInt(nbPlayerCbo.getSelectedItem().toString()), colors, pseudo, gameMode );
                //Passe à la vue GameView
                mainFrame.setGamePanel();
            }
        });
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        mainPnl.add(playBtn, c);
    }

                    /** Rediriger vers la partie **/
    public JPanel getMainPanel(){
            return this;
    }

    public void paint(Graphics g){
        g.drawImage( this.img.getImage(), 0 , 0,mainFrame.getWidth(), mainFrame.getHeight(), null);
        super.paint(g);
    }

    /** Creation des combobox de couleurs pour les joueurs **/
    private void createColorCbo(){
        GridBagConstraints c = new GridBagConstraints();
        int margin = 20;
        c.insets = new Insets(margin, margin, margin, margin);

        KingColor[] colors = KingColor.values();
        GridBagConstraints colorsC = new GridBagConstraints();
        int boardMargin = 20;
        int playerNb = Integer.parseInt( nbPlayerCbo.getSelectedItem().toString() );
        playerColorsPnl.removeAll();
        playerColorsPnl.setBackground( new Color(174,135,0) );

        for (int i = 0; i< playerNb; i++){
            colorsC.gridx = i;
            colorsC.gridy = 20;
            JComboBox cboColors = new JComboBox(colors);
            JTextField name = new JTextField("name");
            cboColors.setFont( FontReader.getInstance().getAlgerian().deriveFont(Font.BOLD).deriveFont(30f) );
            cboColors.setBackground( cboColor );
            cboColors.setSelectedIndex(i);
            cboColors.setFocusable(false);
            name.setFont( FontReader.getInstance().getAlgerian().deriveFont(Font.BOLD).deriveFont(20f) );
            name.setBackground( cboColor );
            name.setPreferredSize(new Dimension(170,30));
            colorsC.insets = new Insets(boardMargin,boardMargin,boardMargin,boardMargin);
            playerColorsPnl.add( cboColors, colorsC);
            colorsC.insets = new Insets(20,40,150,40);
            playerColorsPnl.add(name,colorsC);
        }
        c.gridwidth = 4;
        c.gridx = 0;
        c.gridy = 3;
        mainPnl.add( playerColorsPnl, c );
        mainPnl.revalidate();
        mainPnl.repaint();
    }

    /** Vérification si les combobox ont des couleurs différentes -- true si toutes les couleurs sont différentes **/
    private boolean isDifferentColor(){

        //Parcours tout les cbo de couleurs
        for (Component component : playerColorsPnl.getComponents()) {
            JComboBox cbo = null;
            if (component instanceof JComboBox)
            {
                cbo = (JComboBox) component;
            }

            //Et les compares avec les autres cbo
            for (Component component2 : playerColorsPnl.getComponents()) {
                if((component != component2) && component2 instanceof JComboBox)
                {

                        JComboBox cbo2 = (JComboBox) component2;


                    if((cbo != null) && cbo.getSelectedItem() == cbo2.getSelectedItem())
                    {
                        return false;
                    }
                }
            }
        }
        return true;
    }



}
