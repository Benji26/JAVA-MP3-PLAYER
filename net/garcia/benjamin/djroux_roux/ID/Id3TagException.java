package net.garcia.benjamin.djroux_roux.ID;

public class Id3TagException extends Exception {

    public Id3TagException(Exception exception, String message) {
        super(message + "\n" + exception.toString());
    }

    public Id3TagException(String message) {
        super(message);
    }
}
