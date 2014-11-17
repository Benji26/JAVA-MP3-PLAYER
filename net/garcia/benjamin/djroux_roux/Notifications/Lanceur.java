package net.garcia.benjamin.djroux_roux.Notifications;

import java.io.File;
import net.garcia.benjamin.djroux_roux.Balance;

public class Lanceur {

    public Lanceur(Balance ins,File in) {
        Notifieur notifieur = new Notifieur(ins, in);
        notifieur.start();
    }
}
