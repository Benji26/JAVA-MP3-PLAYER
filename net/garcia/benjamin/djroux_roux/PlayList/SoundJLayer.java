/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.garcia.benjamin.djroux_roux.PlayList;

import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.*;

/**
 *
 * @author Benjamin
 */
public class SoundJLayer extends PlaybackListener implements Runnable {

    private String filePath;
    private Player player;
    private Thread playerThread;

    public SoundJLayer(String filePath) {
        this.filePath = filePath;
    }

    public void stop() {
        try {
            this.playerThread.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void pause() {
        try {
            this.playerThread.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void play(int begin) throws Exception {
        if (player != null) {
            player.play(begin);
        }
    }

    public int getPosition() {
        return player.getPosition();
    }

    public void play() {
        try {
            String urlAsString =
                    "file:///"
                    + new java.io.File(".").getCanonicalPath()
                    + "/"
                    + this.filePath;

            this.player = new Player(
                    new java.net.URL(urlAsString).openStream(),
                    javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());


            this.playerThread = new Thread(this, "AudioPlayerThread");

            this.playerThread.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // PlaybackListener members
    public void playbackStarted(PlaybackEvent playbackEvent) {
        System.out.println("playbackStarted()");
    }

    public void playbackFinished(PlaybackEvent playbackEvent) {
        System.out.println("playbackEnded()");
    }

    // Runnable members
    public void run() {
        try {
            this.player.play();
        } catch (javazoom.jl.decoder.JavaLayerException ex) {
            ex.printStackTrace();
        }

    }
}
