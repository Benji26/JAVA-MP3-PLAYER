package net.garcia.benjamin.djroux_roux.Listener;

import java.io.File;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import net.garcia.benjamin.djroux_roux.Balance;
import net.garcia.benjamin.djroux_roux.BasicPlayer.BasicController;
import net.garcia.benjamin.djroux_roux.BasicPlayer.BasicPlayerEvent;
import net.garcia.benjamin.djroux_roux.BasicPlayer.BasicPlayerListener;
import org.tritonus.share.sampled.file.TAudioFileFormat;

/**
 *
 * @author Benjamin
 */
public class ListenerList implements BasicPlayerListener {

    Balance instance;
    File song;

    public ListenerList(Balance pl, File f) {
        instance = pl;
        song = f;
    }

    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        double time = microseconds / (1000 * 1000);
        int y = (int) Math.round(time / 1);
        instance.progressLecteur1_Temp.setValue(y);

    }

    public String getDurationWithMp3Spi(File file) {

        String fin = "";

        try {
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
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

    @Override
    public void stateUpdated(BasicPlayerEvent event) {
    }

    @Override
    public void setController(BasicController controller) {
    }

    @Override
    public void opened(Object stream, Map properties) {
    }
}
