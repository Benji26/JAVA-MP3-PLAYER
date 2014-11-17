package net.garcia.benjamin.djroux_roux.Notifications;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JWindow;
import net.garcia.benjamin.djroux_roux.Balance;
import net.garcia.benjamin.djroux_roux.Controls.JImagePanel;
import net.garcia.benjamin.djroux_roux.ID.ID3v2Tag;
import org.tritonus.share.sampled.file.TAudioFileFormat;

public class FenetreNotification extends JWindow {

    private Balance pl;
    private static final long serialVersionUID = 227309878747520841L;
    private File file;

    public FenetreNotification(Balance instance, File fil) {
        pl = instance;
        this.file = fil;
        initComponents();
    }

    private void initComponents() {
        ID3v2Tag tag = pl.getTag(file);
        String duration = getDurationWithMp3Spi();
        jLabel1 = new javax.swing.JLabel();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize().getSize();
        setAlwaysOnTop(true);
        setSize(360, 136);
        panel = new JImagePanel();
        image_panel = new JImagePanel();
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(panel);
        panel.setLayout(jPanel1Layout);
        panel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true));
        jLabel1.setFont(new java.awt.Font("Arial", 0, 11));
        jLabel1.setText("<html>Titre: " + tag.getTitre() + "<br/><br/> Album: " + tag.getAlbum() + "<br/><br/> Auteur: " + tag.getArtiste() + " <br/><br/> Durée: " + duration + "</html>");

        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(image_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGap(0, 167, Short.MAX_VALUE)));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(image_panel, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(jLabel1)))
                .addGap(16, 16, 16)));

        try {
            this.image_panel.setImage(pl.jackette.loadJackette(new File(pl.FilePath + "/" + tag.getArtiste()), "png/jpg").getImage());
        } catch (Exception ex) {
            System.out.println("image ne marche pas");
        }

        try {
            panel.setImage("notif.png");
        } catch (IOException e) {
        }
        image_panel.setSize(120, 120);

        add(panel);
        setLocation((int) d.getWidth() - 370, (int) d.getHeight() - 186);
    }

    public String getDurationWithMp3Spi() {

        String fin = "";

        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(this.file);
            if (fileFormat instanceof TAudioFileFormat) {
                Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
                String key = "duration";
                Long microseconds = (Long) properties.get(key);
                int mili = (int) (microseconds / 1000);
                int sec = (mili / 1000) % 60;
                int min = (mili / 1000) / 60;
                fin = min + ":" + sec;
            } else {
                throw new UnsupportedAudioFileException();
            }
        } catch (Exception ex) {
        }
        return fin;

    }
    public javax.swing.JLabel jLabel1;
    private JImagePanel image_panel;
    private JImagePanel panel;
}
