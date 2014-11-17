package net.garcia.benjamin.djroux_roux.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Benjamin
 */
public class FileUtils {
    
   public ArrayList listeFichiers = new ArrayList();
   
   public FileUtils(){
       
   }

    public static ArrayList<String> getFiles(File path) {
        ArrayList<String> filesList = new ArrayList<String>();
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File dir : files) {
                if (dir.getPath().endsWith(".mp3")) {
                    filesList.add(dir.toURI().getPath());
                }
            }
        }
        return filesList;
    }
    
    public void searchAllFiles(File fichier) {
        if (fichier.isDirectory()) {
            File[] sousRepertoire = fichier.listFiles();
            for (int i = 0; i < sousRepertoire.length; i++) {
                searchAllFiles(sousRepertoire[i]);
            }
        }
        listeFichiers.add(fichier.getPath());
    }

    public static File[] getJackette(File path) {
        File[] files = null;
        if (path.exists()) {
            files = path.listFiles();
        }
        return files;
    }
}
