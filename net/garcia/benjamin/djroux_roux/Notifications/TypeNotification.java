package net.garcia.benjamin.djroux_roux.Notifications;

import java.awt.Color;

public class TypeNotification {

    /**
     * Type de notification prédéfinit "erreur"
     */
    public static TypeNotification ERREUR = new TypeNotification(Color.red, Color.WHITE);
    /**
     * Type de notification prédéfinit "information"
     */
    public static TypeNotification INFO = new TypeNotification(new Color(100, 100, 255), Color.WHITE);
    /**
     * Type de notification prédéfinit "confirmation"
     */
    public static TypeNotification VALIDATION = new TypeNotification(Color.GREEN, Color.WHITE);
    /**
     * La couleur du coin haut gauche
     */
    private Color couleur1;
    /**
     * La couleur du coin bas droit
     */
    private Color couleur2;

    /**
     * <b>Constructeur de PanelGradient</b>
     *
     * @param couleur1 La couleur du coin haut gauche
     * @param couleur2 La couleur du coin bas droit
     */
    public TypeNotification(Color couleur1, Color couleur2) {
        this.couleur1 = couleur1;
        this.couleur2 = couleur2;
    }

    /**
     * @return the couleur1
     */
    public Color getCouleur1() {
        return couleur1;
    }

    /**
     * @return the couleur2
     */
    public Color getCouleur2() {
        return couleur2;
    }
}
