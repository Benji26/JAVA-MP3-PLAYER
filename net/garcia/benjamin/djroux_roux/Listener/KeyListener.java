/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.garcia.benjamin.djroux_roux.Listener;

import java.awt.event.KeyEvent;

/**
 *
 * @author Benjamin
 */
public class KeyListener implements java.awt.event.KeyListener {

    public KeyListener() {
    }

    @Override
    public void keyTyped(KeyEvent e) {
       
    }

    @Override
    public void keyPressed(KeyEvent e) {
       if(e.getKeyCode() == KeyEvent.VK_E){
            System.out.println("ok");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
      
    }
    
}
