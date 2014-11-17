/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.garcia.benjamin.djroux_roux.Jackettes;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import net.garcia.benjamin.djroux_roux.Balance;
import net.garcia.benjamin.djroux_roux.Utils.ImageUtils;

/**
 *
 * @author Benjamin
 */
public class JacketteUtils {
    
    Balance instance;
    
    public JacketteUtils(Balance ins) {
        this.instance = ins;
    }
    
    public ImageIcon loadJackette(File file, String type) {
        BufferedImage image = null;
        try {
            if (type.equals("png/jpg")) {
                if (ImageIO.read(new File(file.getPath() + ".png")) != null) {
                    image = ImageIO.read(new File(file.getPath() + ".png"));
                } else if (ImageIO.read(new File(file.getPath() + ".jpg")) != null) {
                    image = ImageIO.read(new File(file.getPath() + ".jpg"));
                }
            }
        } catch (IOException ex) {
            ImageIcon ico = new ImageIcon(instance.getClass().getResource("/net/garcia/benjamin/djroux_roux/icons/unknow.png"));
            Image img = ico.getImage();
            ico = ImageUtils.scale(img, 120, 120);
            return ico;
        }
        Image img = image;
        ImageIcon scale = ImageUtils.scale(img, 120, 120);
        return scale;
    }
    
}
