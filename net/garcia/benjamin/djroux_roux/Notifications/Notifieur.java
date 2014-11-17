package net.garcia.benjamin.djroux_roux.Notifications;

import java.io.File;
import javax.swing.JWindow;
import net.garcia.benjamin.djroux_roux.Balance;

public class Notifieur extends Thread {

    /**
     * La fenêtre de notification
     */
    private JWindow fenetre;
    Balance instance;

    public Notifieur(Balance ins, File fi) {
        fenetre = new FenetreNotification(ins, fi);
        instance = ins;
    }

    @Override
    public void run() {
        fenetre.setVisible(true);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fenetre.setVisible(false);
        fenetre.dispose();
    }
}
