/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.garcia.benjamin.djroux_roux.Utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Benjamin
 */
public class ImageUtils {

    public static ImageIcon scale(Image source, int width, int height) {
        /* On crée une nouvelle image aux bonnes dimensions. */
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        /* On dessine sur le Graphics de l'image bufferisée. */
        Graphics2D g = buf.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();

        /* On retourne l'image bufferisée, qui est une image. */
        ImageIcon ico = new ImageIcon(buf);

        return ico;
    }
    
    public static Image scale(Image source, int width, int height, String h) {
        /* On crée une nouvelle image aux bonnes dimensions. */
        BufferedImage buf = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        /* On dessine sur le Graphics de l'image bufferisée. */
        Graphics2D g = buf.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(source, 0, 0, width, height, null);
        g.dispose();

        /* On retourne l'image bufferisée, qui est une image. */

        return source;
    }
}
