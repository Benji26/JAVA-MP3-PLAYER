package net.garcia.benjamin.djroux_roux;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.garcia.benjamin.djroux_roux.BasicPlayer.BasicController;
import net.garcia.benjamin.djroux_roux.BasicPlayer.BasicPlayer;
import net.garcia.benjamin.djroux_roux.BasicPlayer.BasicPlayerException;
import net.garcia.benjamin.djroux_roux.ID.ID3v2Tag;
import net.garcia.benjamin.djroux_roux.ID.Id3TagException;
import net.garcia.benjamin.djroux_roux.Jackettes.JacketteUtils;
import net.garcia.benjamin.djroux_roux.Listener.KeyListener;
import net.garcia.benjamin.djroux_roux.Listener.ListenerList;
import net.garcia.benjamin.djroux_roux.Notifications.Lanceur;
import net.garcia.benjamin.djroux_roux.Utils.FileUtils;

/**
 *
 * @author Benjamin
 */
public final class Balance extends javax.swing.JFrame {

    //
    // Lecteur n°1
    //
    public BasicPlayer lecteur1 = new BasicPlayer(this);
    public BasicController Player1_playerController = (BasicController) lecteur1;
    public JacketteUtils jackette = new JacketteUtils(this);
    public ArrayList<String> songs = new ArrayList<String>();
    ListenerList Player1_lst;
    public boolean Player1_isPlaying = false;
    public boolean Player1_isPaused = false;
    public int inPlayed = 0;
    public String FilePath = "";
    public int Player1_seconds = 0;
    public int Player1_lastSeconds = 0;
    public int Player1_minutes = 0;
    public int Player1_heures = 0;
    public double Player1_gain = 0.0;
    public Balance th = this;

    //Fin
    /**
     * Creates new form Balance
     */
    public Balance() {
        initComponents();
        loadSongs();
        Lecteur1();
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(new KeyListener());
    }

    public void Lecteur1() {
        Player1_lst = new ListenerList(this, new File(songs.get(inPlayed)));
        lecteur1.addBasicPlayerListener(Player1_lst);
        this.progressLecteur1_Temp.setMaximum(loadTime(new File(songs.get(inPlayed))));

        if (inPlayed == 0) {
            this.buttonLecteur1_previous.setEnabled(false);
        }
        if (inPlayed == songs.size()) {
            this.buttonLecteur1_next.setEnabled(false);
        }


        sliderLecteur1_Volume.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    double value = sliderLecteur1_Volume.getValue() / 100.0;
                    Player1_gain = value;
                    String t = String.valueOf(sliderLecteur1_Volume.getValue());
                    Player1_playerController.setGain(value);
                    if (t.length() == 3) {
                        labelLecteur1_Volume100.setText(t + "%");
                    } else if (t.length() == 2) {
                        labelLecteur1_Volume100.setText(t + "%" + " ");
                    } else if (t.length() == 1) {
                        labelLecteur1_Volume100.setText(t + "%" + "  ");
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        progressLecteur1_Temp.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                try {
                    Player1_seconds = Player1_seconds + 1;
                    if (Player1_seconds == 60) {
                        Player1_minutes = Player1_minutes + 1;
                        Player1_seconds = 0;
                        Player1_lastSeconds = Player1_lastSeconds + 60;
                    }

                    if (Player1_minutes == 60) {
                        Player1_heures = Player1_heures + 1;
                        Player1_minutes = 0;
                    }

                    if (Player1_seconds < 10) {
                        labelLecteur1_Temp.setText(Player1_heures + "" + Player1_minutes + ":0" + Player1_seconds + " - " + Player1_lst.getDurationWithMp3Spi(new File(songs.get(inPlayed))));
                    } else {
                        labelLecteur1_Temp.setText(Player1_heures + "" + Player1_minutes + ":" + Player1_seconds + " - " + Player1_lst.getDurationWithMp3Spi(new File(songs.get(inPlayed))));
                    }

                    String[] labTemp = labelLecteur1_Temp.getText().split(" - ");

                    if (labTemp[0].equals(labTemp[1]) || Integer.valueOf(labTemp[0]) == Integer.valueOf(labTemp[1]) - 1) {
                        inPlayed = inPlayed + 1;
                        if (inPlayed == 0) {
                            buttonLecteur1_previous.setEnabled(false);
                        } else {
                            buttonLecteur1_previous.setEnabled(true);
                        }
                        if (inPlayed + 1 == songs.size()) {
                            buttonLecteur1_next.setEnabled(false);
                        } else {
                            buttonLecteur1_next.setEnabled(true);
                        }
                        try {
                            Player1_playerController.stop();
                            Player1_isPlaying = false;
                            Player1_isPaused = false;
                            progressLecteur1_Temp.setValue(0);
                            Player1_heures = 0;
                            Player1_seconds = 0;
                            Player1_minutes = 0;
                            Player1_lastSeconds = 0;
                            labelLecteur1_Temp.setText("00:00 - 00:00");
                            ImageIcon ico = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/play.png"));
                            buttonLecteur1_play_pause.setIcon(ico);
                            Player1_playerController.open(new File(songs.get(inPlayed)));
                            try {
                                //MP3File mp3file = new MP3File(song);
                                ID3v2Tag tag = new ID3v2Tag(songs.get(inPlayed), "r");
                                labelLecteur1_Titre1.setText(tag.getArtiste() + " - " + tag.getTitre());
                                labelLecteur1_image.setIcon(jackette.loadJackette(new File(FilePath + "/" + tag.getArtiste()), "png/jpg"));
                                try {
                                    Lanceur lan = new Lanceur(th, new File(songs.get(inPlayed)));
                                } catch (Exception ex) {
                                }

                            } catch (Exception ex) {
                                Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            Player1_isPlaying = true;
                            progressLecteur1_Temp.setMaximum(loadTime(new File(songs.get(inPlayed))));
                            Player1_playerController.play();
                            if (Player1_gain != 0.0) {
                                try {
                                    Player1_playerController.setGain(Player1_gain);
                                } catch (BasicPlayerException ex) {
                                    Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            ImageIcon ico2 = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/pause.png"));
                            buttonLecteur1_play_pause.setIcon(ico2);
                        } catch (BasicPlayerException ex) {
                            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    public ID3v2Tag getTag(File f) {
        ID3v2Tag tag = null;
        try {
            tag = new ID3v2Tag(f.getPath(), "r");
        } catch (Id3TagException ex) {
            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tag;
    }

    public int loadTime(File f) {
        String temp = Player1_lst.getDurationWithMp3Spi(f);

        String[] fi = temp.split(":");

        int min = Integer.valueOf(fi[0]);
        int sec = Integer.valueOf(fi[1]);

        int minutes = 60 * min;

        int fin = minutes + sec;

        return fin;
    }

    public void loadSongs() {
        if (!FilePath.equals("")) {
            songs = FileUtils.getFiles(new File(FilePath));
        } else {
            int returnVal = this.jFileChooser2.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = this.jFileChooser2.getSelectedFile();
                FilePath = this.jFileChooser2.getSelectedFile().getPath();
                try {
                    songs = FileUtils.getFiles(file);
                } catch (Exception ex) {
                    System.out.println("problem accessing file" + file.getAbsolutePath());
                }
            } else {
                System.out.println("File access cancelled by user.");
            }
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser2 = new javax.swing.JFileChooser();
        panel_main = new javax.swing.JPanel();
        panelLecteur1 = new javax.swing.JPanel();
        labelLecteur1_Titre = new javax.swing.JLabel();
        labelLecteur1_Temp = new javax.swing.JLabel();
        labelLecteur1_image = new javax.swing.JLabel();
        labelLecteur1_Titre1 = new javax.swing.JLabel();
        panelLecteur1_Name = new javax.swing.JPanel();
        labelLecteur1_Name = new javax.swing.JLabel();
        buttonLecteur1_Settings = new javax.swing.JButton();
        panelLecteur1_Controles = new javax.swing.JPanel();
        buttonLecteur1_previous = new javax.swing.JButton();
        buttonLecteur1_play_pause = new javax.swing.JButton();
        buttonLecteur1_next = new javax.swing.JButton();
        buttonLecteur1_stop = new javax.swing.JButton();
        panelLecteur1_Son = new javax.swing.JPanel();
        labelLecteur1_Volume = new javax.swing.JLabel();
        sliderLecteur1_Volume = new javax.swing.JSlider();
        labelLecteur1_Volume100 = new javax.swing.JLabel();
        progressLecteur1_Temp = new javax.swing.JProgressBar();

        jFileChooser2.setCurrentDirectory(new java.io.File("C:\\Users\\Benjamin\\Music"));
        jFileChooser2.setDialogTitle("Choisir un dossier");
        jFileChooser2.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Dj Roux-Roux Balance");
        setBackground(new java.awt.Color(153, 153, 0));

        panel_main.setBackground(new java.awt.Color(153, 153, 153));

        panelLecteur1.setBackground(new java.awt.Color(51, 51, 51));

        labelLecteur1_Titre.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        labelLecteur1_Titre.setText("Titre:");

        labelLecteur1_Temp.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        labelLecteur1_Temp.setText("00:00 - 00:00");

        labelLecteur1_Titre1.setText("-");

        panelLecteur1_Name.setBackground(new java.awt.Color(102, 102, 102));

        labelLecteur1_Name.setBackground(new java.awt.Color(204, 204, 204));
        labelLecteur1_Name.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        labelLecteur1_Name.setText("Lecteur n°1");

        buttonLecteur1_Settings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/settings.png"))); // NOI18N
        buttonLecteur1_Settings.setBorder(null);

        javax.swing.GroupLayout panelLecteur1_NameLayout = new javax.swing.GroupLayout(panelLecteur1_Name);
        panelLecteur1_Name.setLayout(panelLecteur1_NameLayout);
        panelLecteur1_NameLayout.setHorizontalGroup(
            panelLecteur1_NameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLecteur1_NameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLecteur1_Name)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonLecteur1_Settings, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        panelLecteur1_NameLayout.setVerticalGroup(
            panelLecteur1_NameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonLecteur1_Settings, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
            .addComponent(labelLecteur1_Name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelLecteur1_Controles.setBackground(new java.awt.Color(51, 51, 51));
        panelLecteur1_Controles.setMaximumSize(new java.awt.Dimension(230, 34));
        panelLecteur1_Controles.setMinimumSize(new java.awt.Dimension(230, 34));

        buttonLecteur1_previous.setBackground(new java.awt.Color(51, 51, 51));
        buttonLecteur1_previous.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/rewind.png"))); // NOI18N
        buttonLecteur1_previous.setBorder(null);
        buttonLecteur1_previous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLecteur1_previousActionPerformed(evt);
            }
        });

        buttonLecteur1_play_pause.setBackground(new java.awt.Color(51, 51, 51));
        buttonLecteur1_play_pause.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/play.png"))); // NOI18N
        buttonLecteur1_play_pause.setBorder(null);
        buttonLecteur1_play_pause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLecteur1_play_pauseActionPerformed(evt);
            }
        });

        buttonLecteur1_next.setBackground(new java.awt.Color(51, 51, 51));
        buttonLecteur1_next.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/next.png"))); // NOI18N
        buttonLecteur1_next.setBorder(null);
        buttonLecteur1_next.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLecteur1_nextActionPerformed(evt);
            }
        });

        buttonLecteur1_stop.setBackground(new java.awt.Color(51, 51, 51));
        buttonLecteur1_stop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/stop.png"))); // NOI18N
        buttonLecteur1_stop.setBorder(null);
        buttonLecteur1_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonLecteur1_stopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelLecteur1_ControlesLayout = new javax.swing.GroupLayout(panelLecteur1_Controles);
        panelLecteur1_Controles.setLayout(panelLecteur1_ControlesLayout);
        panelLecteur1_ControlesLayout.setHorizontalGroup(
            panelLecteur1_ControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLecteur1_ControlesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonLecteur1_previous, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonLecteur1_play_pause, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonLecteur1_next, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(buttonLecteur1_stop, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        panelLecteur1_ControlesLayout.setVerticalGroup(
            panelLecteur1_ControlesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(buttonLecteur1_play_pause, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(buttonLecteur1_stop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(buttonLecteur1_previous, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(buttonLecteur1_next, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panelLecteur1_Son.setBackground(new java.awt.Color(51, 51, 51));
        panelLecteur1_Son.setFocusable(false);
        panelLecteur1_Son.setMaximumSize(new java.awt.Dimension(142, 26));
        panelLecteur1_Son.setMinimumSize(new java.awt.Dimension(142, 26));

        labelLecteur1_Volume.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        labelLecteur1_Volume.setText("Volume:");

        sliderLecteur1_Volume.setValue(100);

        labelLecteur1_Volume100.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        labelLecteur1_Volume100.setText("100%");

        javax.swing.GroupLayout panelLecteur1_SonLayout = new javax.swing.GroupLayout(panelLecteur1_Son);
        panelLecteur1_Son.setLayout(panelLecteur1_SonLayout);
        panelLecteur1_SonLayout.setHorizontalGroup(
            panelLecteur1_SonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLecteur1_SonLayout.createSequentialGroup()
                .addComponent(labelLecteur1_Volume)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sliderLecteur1_Volume, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(labelLecteur1_Volume100))
        );
        panelLecteur1_SonLayout.setVerticalGroup(
            panelLecteur1_SonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sliderLecteur1_Volume, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
            .addComponent(labelLecteur1_Volume, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(labelLecteur1_Volume100, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        progressLecteur1_Temp.setBackground(new java.awt.Color(255, 153, 153));

        javax.swing.GroupLayout panelLecteur1Layout = new javax.swing.GroupLayout(panelLecteur1);
        panelLecteur1.setLayout(panelLecteur1Layout);
        panelLecteur1Layout.setHorizontalGroup(
            panelLecteur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelLecteur1_Name, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(panelLecteur1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelLecteur1_image, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelLecteur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLecteur1Layout.createSequentialGroup()
                        .addGroup(panelLecteur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labelLecteur1_Temp)
                            .addGroup(panelLecteur1Layout.createSequentialGroup()
                                .addComponent(labelLecteur1_Titre)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labelLecteur1_Titre1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelLecteur1Layout.createSequentialGroup()
                        .addGroup(panelLecteur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panelLecteur1Layout.createSequentialGroup()
                                .addComponent(panelLecteur1_Son, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                                .addComponent(panelLecteur1_Controles, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(progressLecteur1_Temp, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        panelLecteur1Layout.setVerticalGroup(
            panelLecteur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLecteur1Layout.createSequentialGroup()
                .addComponent(panelLecteur1_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelLecteur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelLecteur1_image)
                    .addGroup(panelLecteur1Layout.createSequentialGroup()
                        .addGroup(panelLecteur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(labelLecteur1_Titre)
                            .addComponent(labelLecteur1_Titre1))
                        .addGap(16, 16, 16)
                        .addComponent(labelLecteur1_Temp)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(progressLecteur1_Temp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addGroup(panelLecteur1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panelLecteur1_Controles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelLecteur1_Son, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout panel_mainLayout = new javax.swing.GroupLayout(panel_main);
        panel_main.setLayout(panel_mainLayout);
        panel_mainLayout.setHorizontalGroup(
            panel_mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelLecteur1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        panel_mainLayout.setVerticalGroup(
            panel_mainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelLecteur1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_main, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_main, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @SuppressWarnings("empty-statement")
    private void buttonLecteur1_play_pauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLecteur1_play_pauseActionPerformed
        if (inPlayed == 0) {
            this.buttonLecteur1_previous.setEnabled(false);
        }
        if (inPlayed + 1 == songs.size()) {
            this.buttonLecteur1_next.setEnabled(false);
        }

        if (this.Player1_isPlaying == true) {
            ImageIcon ico = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/play.png"));
            this.buttonLecteur1_play_pause.setIcon(ico);
            Player1_isPlaying = false;
            try {
                Player1_playerController.pause();
                Player1_isPaused = true;
            } catch (BasicPlayerException ex) {
                Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            ImageIcon ico = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/pause.png"));
            this.buttonLecteur1_play_pause.setIcon(ico);
            try {
                //MP3File mp3file = new MP3File(song);
                ID3v2Tag tag = new ID3v2Tag(songs.get(inPlayed), "r");
                this.labelLecteur1_Titre1.setText(tag.getArtiste() + " - " + tag.getTitre());
                this.labelLecteur1_image.setIcon(jackette.loadJackette(new File(FilePath + "/" + tag.getArtiste()), "png/jpg"));
                try {
                    if (Player1_isPaused) {
                        Player1_playerController.resume();
                        Player1_isPaused = false;
                    } else {
                        Lanceur lan = new Lanceur(this, new File(songs.get(inPlayed)));
                        Player1_playerController.open(new File(songs.get(inPlayed)));
                        Player1_playerController.play();
                    }



                } catch (Exception ex) {
                }

            } catch (Exception ex) {
                Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
            }
            Player1_isPlaying = true;
        }
        if (Player1_gain != 0.0) {
            try {
                Player1_playerController.setGain(Player1_gain);
            } catch (BasicPlayerException ex) {
                Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.progressLecteur1_Temp.setMaximum(loadTime(new File(songs.get(inPlayed))));
    }//GEN-LAST:event_buttonLecteur1_play_pauseActionPerformed

    private void buttonLecteur1_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLecteur1_stopActionPerformed
        if (inPlayed == 0) {
            this.buttonLecteur1_previous.setEnabled(false);
        }
        if (inPlayed == songs.size()) {
            this.buttonLecteur1_next.setEnabled(false);
        }
        try {
            Player1_playerController.stop();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
        }
        Player1_isPlaying = false;
        Player1_isPaused = false;
        progressLecteur1_Temp.setValue(0);
        Player1_heures = 0;
        Player1_seconds = 0;
        Player1_minutes = 0;
        Player1_lastSeconds = 0;
        labelLecteur1_Temp.setText("00:00 - 00:00");
        ImageIcon ico = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/play.png"));
        this.buttonLecteur1_play_pause.setIcon(ico);
    }//GEN-LAST:event_buttonLecteur1_stopActionPerformed

    private void buttonLecteur1_previousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLecteur1_previousActionPerformed
        inPlayed = inPlayed - 1;
        if (inPlayed == 0) {
            this.buttonLecteur1_previous.setEnabled(false);
        } else {
            this.buttonLecteur1_previous.setEnabled(true);
        }
        if (inPlayed + 1 == songs.size()) {
            this.buttonLecteur1_next.setEnabled(false);
        } else {
            this.buttonLecteur1_next.setEnabled(true);
        }
        try {
            Player1_playerController.stop();
            Player1_isPlaying = false;
            Player1_isPaused = false;
            progressLecteur1_Temp.setValue(0);
            Player1_heures = 0;
            Player1_seconds = 0;
            Player1_minutes = 0;
            Player1_lastSeconds = 0;
            labelLecteur1_Temp.setText("00:00 - 00:00");
            ImageIcon ico = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/play.png"));
            buttonLecteur1_play_pause.setIcon(ico);
            Player1_playerController.open(new File(songs.get(inPlayed)));
            try {
                //MP3File mp3file = new MP3File(song);
                ID3v2Tag tag = new ID3v2Tag(songs.get(inPlayed), "r");
                this.labelLecteur1_Titre1.setText(tag.getArtiste() + " - " + tag.getTitre());
                this.labelLecteur1_image.setIcon(jackette.loadJackette(new File(FilePath + "/" + tag.getArtiste()), "png/jpg"));
                try {
                    Lanceur lan = new Lanceur(this, new File(songs.get(inPlayed)));




                } catch (Exception ex) {
                }

            } catch (Exception ex) {
                Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
            }
            Player1_isPlaying = true;
            this.progressLecteur1_Temp.setMaximum(loadTime(new File(songs.get(inPlayed))));
            Player1_playerController.play();
            if (Player1_gain != 0.0) {
                try {
                    Player1_playerController.setGain(Player1_gain);
                } catch (BasicPlayerException ex) {
                    Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ImageIcon ico2 = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/pause.png"));
            buttonLecteur1_play_pause.setIcon(ico2);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buttonLecteur1_previousActionPerformed

    private void buttonLecteur1_nextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonLecteur1_nextActionPerformed
        inPlayed = inPlayed + 1;
        if (inPlayed == 0) {
            this.buttonLecteur1_previous.setEnabled(false);
        } else {
            this.buttonLecteur1_previous.setEnabled(true);
        }
        if (inPlayed + 1 == songs.size()) {
            this.buttonLecteur1_next.setEnabled(false);
        } else {
            this.buttonLecteur1_next.setEnabled(true);
        }
        try {
            Player1_playerController.stop();
            Player1_isPlaying = false;
            Player1_isPaused = false;
            progressLecteur1_Temp.setValue(0);
            Player1_heures = 0;
            Player1_seconds = 0;
            Player1_minutes = 0;
            Player1_lastSeconds = 0;
            labelLecteur1_Temp.setText("00:00 - 00:00");
            ImageIcon ico = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/play.png"));
            buttonLecteur1_play_pause.setIcon(ico);
            Player1_playerController.open(new File(songs.get(inPlayed)));
            try {
                //MP3File mp3file = new MP3File(song);
                ID3v2Tag tag = new ID3v2Tag(songs.get(inPlayed), "r");
                this.labelLecteur1_Titre1.setText(tag.getArtiste() + " - " + tag.getTitre());
                this.labelLecteur1_image.setIcon(jackette.loadJackette(new File(FilePath + "/" + tag.getArtiste()), "png/jpg"));
                try {
                    Lanceur lan = new Lanceur(this, new File(songs.get(inPlayed)));
                } catch (Exception ex) {
                }

            } catch (Exception ex) {
                Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
            }
            Player1_isPlaying = true;
            this.progressLecteur1_Temp.setMaximum(loadTime(new File(songs.get(inPlayed))));
            Player1_playerController.play();
            if (Player1_gain != 0.0) {
                try {
                    Player1_playerController.setGain(Player1_gain);
                } catch (BasicPlayerException ex) {
                    Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ImageIcon ico2 = new ImageIcon(getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/pause.png"));
            buttonLecteur1_play_pause.setIcon(ico2);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buttonLecteur1_nextActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Balance.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        try {
            for (UIManager.LookAndFeelInfo laf : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(laf.getName())) {
                    UIManager.setLookAndFeel(laf.getClassName());
                    UIManager.getLookAndFeelDefaults().put("nimbusOrange", (new Color(153, 0, 153)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Balance().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonLecteur1_Settings;
    private javax.swing.JButton buttonLecteur1_next;
    public javax.swing.JButton buttonLecteur1_play_pause;
    private javax.swing.JButton buttonLecteur1_previous;
    private javax.swing.JButton buttonLecteur1_stop;
    private javax.swing.JFileChooser jFileChooser2;
    private javax.swing.JLabel labelLecteur1_Name;
    public static javax.swing.JLabel labelLecteur1_Temp;
    private javax.swing.JLabel labelLecteur1_Titre;
    private javax.swing.JLabel labelLecteur1_Titre1;
    private javax.swing.JLabel labelLecteur1_Volume;
    private javax.swing.JLabel labelLecteur1_Volume100;
    private javax.swing.JLabel labelLecteur1_image;
    private javax.swing.JPanel panelLecteur1;
    private javax.swing.JPanel panelLecteur1_Controles;
    private javax.swing.JPanel panelLecteur1_Name;
    private javax.swing.JPanel panelLecteur1_Son;
    private javax.swing.JPanel panel_main;
    public javax.swing.JProgressBar progressLecteur1_Temp;
    public static javax.swing.JSlider sliderLecteur1_Volume;
    // End of variables declaration//GEN-END:variables
}
