package net.garcia.benjamin.djroux_roux.ID;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;

public class ID3v1Tag {

    /**
     * Indicateur pour dire si "true" qu'on utilise la r�vision 1 des tags
     * id3v1. s'il vaut "false", on utilise la version originale
     */
    private boolean id3v11 = false;
    /**
     * Indicateur d'existence du tag dans le fichier.
     */
    private boolean existTag = false;
    /**
     * Si � TRUE indique que le tag devra �tre supprim� lors du prochain
     * enregistrement.
     */
    private boolean tagDesactive = true;
    /**
     * Si � TRUE indique que l'objet est en lecture seule.
     */
    private boolean modeLectureSeule = false;
    /**
     * Si � TRUE indique que l'objet a �t� modifi�.
     */
    private boolean existModification = false;
    /**
     * Le titre de la chanson dans l'objet.
     */
    private String titre = "";
    /**
     * L'artiste de la chanson dans l'objet.
     */
    private String artiste = "";
    /**
     * L'album de la chanson dans l'objet.
     */
    private String album = "";
    /**
     * L'ann�e de la chanson dans l'objet.
     */
    private String annee = "";
    /**
     * Le commentaire sur la chanson dans l'objet.
     */
    private String commentaire = "";
    /**
     * Le num�ro (dans l'album) de la chanson dans l'objet.
     */
    private String numChanson = "";
    /**
     * Le style de la chanson dans l'objet (Voir table de correspondance).
     */
    private int genre;
    /**
     * Le fichier sur lequel on cr�e l'objet.
     */
    RandomAccessFile fichier = null;
    /**
     * La liste de tous les styles d�finis. Le num�ro associ� est donn� par la
     * position dans le tableau.
     */
    public static final String[] TAB_STYLES_MUSIQUES_ID3V1 = {"Blues", "Classic Rock", "Country", "Dance", "Disco",
        "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age",
        "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock",
        "Techno", "Industrial", "Alternative", "Ska",
        "Death Metal", "Pranks", "Soundtrack", "Euro-Techno",
        "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion",
        "Trance", "Classical", "Instrumental", "Acid", "House",
        "Game", "Sound Clip", "Gospel", "Noise", "AlternRock",
        "Bass", "Soul", "Punk", "Space", "Meditative",
        "Instrumental Pop", "Instrumental Rock", "Ethnic",
        "Gothic", "Darkwave", "Techno-Industrial", "Electronic",
        "Pop-Folk", "Eurodance", "Dream", "Southern Rock",
        "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap",
        "Pop/Funk", "Jungle", "Native American", "Cabaret",
        "New Wave", "Psychadelic", "Rave", "Showtunes",
        "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz",
        "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock",
        "Folk", "Folk-Rock", "National Folk", "Swing",
        "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic",
        "Bluegrass", "Avantgarde", "Gothic Rock",
        "Progressive Rock", "Psychedelic Rock", "Symphonic Rock",
        "Slow Rock", "Big Band", "Chorus", "Easy Listening",
        "Acoustic", "Humour", "Speech", "Chanson", "Opera",
        "Chamber Music", "Sonata", "Symphony", "Booty Bass",
        "Primus", "Porn Groove", "Satire", "Slow Jam", "Club",
        "Tango", "Samba", "Folklore", "Ballad", "Power Ballad",
        "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock",
        "Drum Solo", "A capella", "Euro-House", "Dance Hall",
        "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror",
        "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat",
        "Christian Gangsta", "Heavy Metal", "Black Metal",
        "Crossover", "Contemporary Christian", "Christian Rock",
        "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop",
        "SynthPop"};

    /**
     * Constructeur de la classe.
     *
     * @param file Chemin absolue du fichier dont on veut �diter le tag ID3v1
     * @param mode "r" pour lecture seule, "rw" pour lecture et �criture.
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * Voir les modes de la classe RandomAccesFile pour plus d'informations.</P>
     * @throws Id3TagException Le type d'exception lev�e en cas d'erreur.
     */
    public ID3v1Tag(String file, String mode) throws Id3TagException {
        this(file, mode, true);
    }

    /**
     * Constructeur de la classe.
     *
     * @param file Chemin absolue du fichier dont on veut �diter le tag ID3v1
     * @param mode "r" pour lecture seule, "rw" pour lecture et �criture.
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * Voir les modes de la classe RandomAccesFile pour plus d'informations.</P>
     * @param recupDonnees Si FALSE, l'objet ne contiendra que les informations
     * d'existence du tag et sa r�vision s'il existe.
     * <BR>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
     * Si TRUE, l'objet contiendra toutes les donn�es du tag.</P>
     * @throws Id3TagException Le type d'exception lev�e en cas d'erreur.
     */
    public ID3v1Tag(String file, String mode, boolean recupDonnees) throws Id3TagException {

        if (mode.equals("r")) {
            this.modeLectureSeule = true;
        }

        try {
            this.fichier = new RandomAccessFile(file, mode);
        } catch (FileNotFoundException e1) {
            throw new Id3TagException(e1, "Impossible de cr�er l'objet d'acc�s au fichier");
        }

        byte[] buffer = new byte[128];

        try {
            if (this.fichier.length() < 128) {
                this.existTag = false;
                return;
            }
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de lire le contenu du fichier");
        }

        // Recherche du mot "TAG" qui introduit le tag
        try {
            this.fichier.seek(this.fichier.length() - 128);
            this.fichier.read(buffer);
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de lire le mot \"TAG\" qui pr�c�de les tags");
        }

        if (!new String(buffer, 0, 3).equals("TAG")) {
            this.existTag = false;
            return;
        }

        this.existTag = true;
        this.tagDesactive = false;

        // Si on est en id3v1.1 alors le troisi�me octet en partant de la fin du fichier doit �tre "0"
        if (buffer[125] == 0) {
            this.id3v11 = true;
        }

        if (!recupDonnees) {
            return;
        }

        //--------------------------------------------------------------
        //-------------------- Contenu du tag TITRE --------------------
        //--------------------------------------------------------------
        try {
            this.titre = new String(buffer, 3, 30, "ISO-8859-1").trim();
        } catch (UnsupportedEncodingException e) {
            throw new Id3TagException(e, "Impossible de r�cup�rer le contenu du tag \"titre\"");
        }

        //---------------------------------------------------------------
        //-------------------- Contenu du tag AUTEUR --------------------
        //---------------------------------------------------------------
        try {
            this.artiste = new String(buffer, 33, 30, "ISO-8859-1").trim();
        } catch (UnsupportedEncodingException e) {
            throw new Id3TagException(e, "Impossible de r�cup�rer le contenu du tag \"auteur\"");
        }

        //--------------------------------------------------------------
        //-------------------- Contenu du tag ALBUM --------------------
        //--------------------------------------------------------------
        try {
            this.album = new String(buffer, 63, 30, "ISO-8859-1").trim();
        } catch (UnsupportedEncodingException e) {
            throw new Id3TagException(e, "Impossible de r�cup�rer le contenu du tag \"album\"");
        }

        //--------------------------------------------------------------
        //-------------------- Contenu du tag ANNEE --------------------
        //--------------------------------------------------------------
        try {
            this.annee = new String(buffer, 93, 4, "ISO-8859-1").trim();
        } catch (UnsupportedEncodingException e) {
            throw new Id3TagException(e, "Impossible de r�cup�rer le contenu du tag \"annee\"");
        }

        //----------------------------------------------------------------
        //-------------------- Contenu du tag COMMENT --------------------
        //----------------------------------------------------------------
        if (this.id3v11) {
            try {
                this.commentaire = new String(buffer, 97, 28, "ISO-8859-1").trim();
            } catch (UnsupportedEncodingException e) {
                throw new Id3TagException(e, "Impossible de r�cup�rer le contenu du tag \"commentaire\"");
            }

            //--------------------------------------------------------------
            //-------------------- Contenu du tag TRACK --------------------
            //--------------------------------------------------------------
            this.numChanson = new Byte(buffer[126]).toString();
        } else {
            try {
                this.commentaire = new String(buffer, 97, 30, "ISO-8859-1").trim();
            } catch (UnsupportedEncodingException e) {
                throw new Id3TagException(e, "Impossible de r�cup�rer le contenu du tag \"commentaire\"");
            }
        }

        //--------------------------------------------------------------
        //-------------------- Contenu du tag GENRE --------------------
        //--------------------------------------------------------------
        this.genre = buffer[127] & 0xFF;
    }

    /**
     * @return Le contenu du tag �quivalent � l'artiste.
     */
    public String getArtiste() {
        return this.artiste;
    }

    /**
     * @return Le contenu du tag �quivalent au titre de la chanson.
     */
    public String getTitre() {
        return this.titre;
    }

    /**
     * @return Le contenu du tag �quivalent � l'album de la chanson.
     */
    public String getAlbum() {
        return this.album;
    }

    /**
     * @return Le contenu du tag �quivalent au commentaire.
     */
    public String getCommentaire() {
        return this.commentaire;
    }

    /**
     * @return Le contenu du tag �quivalent � l'ann�e de la chanson.
     */
    public String getAnnee() {
        return this.annee;
    }

    /**
     * @return Le contenu du tag �quivalent au num�ro de la chanson.
     */
    public String getNumChanson() {
        if (this.id3v11) {
            if (this.numChanson.equals("0")) {
                return "";
            }

            return this.numChanson;
        }

        return "";
    }

    /**
     * @return Le contenu du tag �quivalent au genre de la chanson (Voir tableau
     * des correspondances).
     */
    public int getGenre() {
        return this.genre;
    }

    /**
     * @return 1 si la version du tag 1.1, 0 si la version du tag est 1.0.
     */
    public int getRevision() {
        if (this.id3v11) {
            return 1;
        }

        return 0;
    }

    /**
     * Modifie la valeur du champ album dans l'objet dans le cas o� l'on permet
     * la modification du fichier. Si l'objet a �t� d�fini en lecture seule,
     * cette m�thode est sans effet. <BR><BR>ATTENTION 1 : A cause des
     * limitations des tags ID3v1, si on passe une chaine de plus de 30
     * caract�res, on ne garde que les 30 premiers. <BR><BR>ATTENTION 2 : Pour
     * que la modification soit prise en compte dans le fichier m�me, il faut
     * appeler la m�thode d'enregistrement "recordTag()".
     *
     * @param album Le contenu qu'on souhaite mettre dans le champ album.
     */
    public void setAlbum(String album) {
        if (!this.modeLectureSeule && !album.equals(this.album)) {
            if (album.length() > 30) {
                album = album.substring(0, 30);
            }

            this.album = album;
            this.existModification = true;
        }
    }

    /**
     * Modifie la valeur du champ annee dans l'objet dans le cas o� l'on permet
     * la modification du fichier. Si l'objet a �t� d�fini en lecture seule,
     * cette m�thode est sans effet. <BR><BR>ATTENTION 1 : A cause des
     * limitations des tags ID3v1, si on passe une chaine de plus de 4
     * caract�res, on ne garde que les 4 premiers. <BR><BR>ATTENTION 2 : Pour
     * que la modification soit prise en compte dans le fichier m�me, il faut
     * appeler la m�thode d'enregistrement "recordTag()".
     *
     * @param annee Le contenu qu'on souhaite mettre dans le champ album.
     */
    public void setAnnee(String annee) {
        if (!this.modeLectureSeule && !annee.equals(this.annee)) {
            if (annee.length() > 4) {
                annee = annee.substring(0, 4);
            }

            this.annee = annee;
            this.existModification = true;
        }
    }

    /**
     * Modifie la valeur du champ artiste dans l'objet dans le cas o� l'on
     * permet la modification du fichier. Si l'objet a �t� d�fini en lecture
     * seule, cette m�thode est sans effet. <BR><BR>ATTENTION 1 : A cause des
     * limitations des tags ID3v1, si on passe une chaine de plus de 30
     * caract�res, on ne garde que les 30 premiers. <BR><BR>ATTENTION 2 : Pour
     * que la modification soit prise en compte dans le fichier m�me, il faut
     * appeler la m�thode d'enregistrement "recordTag()".
     *
     * @param album Le contenu qu'on souhaite mettre dans le champ album.
     */
    public void setArtiste(String artiste) {
        if (!this.modeLectureSeule && !artiste.equals(this.artiste)) {
            if (artiste.length() > 30) {
                artiste = artiste.substring(0, 30);
            }

            this.artiste = artiste;
            this.existModification = true;
        }
    }

    /**
     * Modifie la valeur du champ commentaire dans l'objet dans le cas o� l'on
     * permet la modification du fichier. Si l'objet a �t� d�fini en lecture
     * seule, cette m�thode est sans effet. <BR><BR>ATTENTION 1 : A cause des
     * limitations des tags ID3v1, si on passe une chaine de plus de 28
     * caract�res, on ne garde que les 28 premiers (On n'enregistre que des tags
     * en version 1.1). <BR><BR>ATTENTION 2 : Pour que la modification soit
     * prise en compte dans le fichier m�me, il faut appeler la m�thode
     * d'enregistrement "recordTag()".
     *
     * @param album Le contenu qu'on souhaite mettre dans le champ album.
     */
    public void setCommentaire(String commentaire) {
        if (!this.modeLectureSeule && !commentaire.equals(this.commentaire)) {
            if (commentaire.length() > 28) {
                commentaire = commentaire.substring(0, 28);
            }

            this.commentaire = commentaire;
            this.existModification = true;
        }
    }

    /**
     * Modifie la valeur du champ titre dans l'objet dans le cas o� l'on permet
     * la modification du fichier. Si l'objet a �t� d�fini en lecture seule,
     * cette m�thode est sans effet. <BR><BR>ATTENTION 1 : A cause des
     * limitations des tags ID3v1, si on passe une chaine de plus de 30
     * caract�res, on ne garde que les 30 premiers. <BR><BR>ATTENTION 2 : Pour
     * que la modification soit prise en compte dans le fichier m�me, il faut
     * appeler la m�thode d'enregistrement "recordTag()".
     *
     * @param album Le contenu qu'on souhaite mettre dans le champ album.
     */
    public void setTitre(String titre) {
        if (!this.modeLectureSeule && !titre.equals(this.titre)) {
            if (titre.length() > 30) {
                titre = titre.substring(0, 30);
            }

            this.titre = titre;
            this.existModification = true;
        }
    }

    /**
     * Permet d'enregistrer les modification de l'objet dans le fichier qui est
     * pass� en param�tre de l'objet. Pour que la mise � jour se fasse, il faut
     * que : <BR><DD><LI>L'objet ne soit pas en lecture seule. <BR><DD><LI>Des
     * modifications existent dans l'objet. <BR><DD><LI>Le tag doit exister pour
     * qu'on puisse le supprimer <BR><BR>
     *
     * @return TRUE si une mise � jour a �t� faite, FALSE sinon.
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public boolean recordTag() throws Id3TagException {

        // Si on n'est pas en mode lecture seule et que l'objet a �t� modifi�...
        if (this.fichier != null && !this.modeLectureSeule && this.existModification) {

            try {
                // Si on veut supprimer le tag...
                if (this.tagDesactive) {
                    // Un tag doit exister...car on ne va pas tronquer le fichier
                    // de 128 octets s'il n'y a pas de tag.
                    if (this.existTag) {
                        // On veut suppimer le tag, le mieux est donc de tronquer le fichier de 128 octets
                        this.fichier.setLength(fichier.length() - 128);
                        this.existTag = false;
                        this.titre = "";
                        this.artiste = "";
                        this.album = "";
                        this.annee = "";
                        this.commentaire = "";
                        this.numChanson = "";
                        this.genre = 0;
                    } else {
                        return false;
                    }
                } else {
                    String tagString = "TAG";
                    String tempTitre = this.titre;
                    String tempArtiste = this.artiste;
                    String tempAlbum = this.album;
                    String tempAnnee = this.annee;
                    String tempComment = this.commentaire;

                    for (int i = this.titre.length(); i < 30; i++) {
                        tempTitre += "\u0000";
                    }

                    tagString += tempTitre;

                    for (int i = this.artiste.length(); i < 30; i++) {
                        tempArtiste += "\u0000";
                    }

                    tagString += tempArtiste;

                    for (int i = this.album.length(); i < 30; i++) {
                        tempAlbum += "\u0000";
                    }

                    tagString += tempAlbum;

                    for (int i = this.annee.length(); i < 4; i++) {
                        tempAnnee += "\u0000";
                    }

                    tagString += tempAnnee;

                    // Dans le cas o� on a un tag id3v1.0, il est sur 30 caract�res => on le tronque
                    if (tempComment.length() > 28) {
                        tempComment = tempComment.substring(0, 28);
                    }

                    for (int i = this.commentaire.length(); i < 28; i++) {
                        tempComment += "\u0000";
                    }

                    tagString += tempComment + "\u0000";

                    // Cas normalement impossible
                    if (tagString.length() != 126) {
                        throw new Id3TagException("erreur longueur : " + tagString.length());
                    }

                    if (this.existTag) {
                        fichier.seek(fichier.length() - 128);
                    } else {
                        fichier.seek(fichier.length());
                    }
                    // Cette m�thode �crase les donn�es existantes...c'est pour �a qu'on
                    // n'a pas besion de distinguer le cas o� le tag existe dans le fichier
                    // de celui o� le tag n'existe pas.
                    fichier.writeBytes(tagString);

                    if (existTag) {
                        fichier.seek(fichier.length() - 2);
                    } else {
                        fichier.seek(fichier.length());
                    }

                    if (this.numChanson.equals("")) {
                        fichier.write(0);
                    } else {
                        fichier.write(new Integer(this.numChanson).byteValue() & 0xFF);
                    }

                    if (existTag) {
                        fichier.seek(fichier.length() - 1);
                    } else {
                        fichier.seek(fichier.length());
                    }
                    fichier.write(this.genre);
                    this.existTag = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new Id3TagException(e, "Impossible de mettre � jour le tag !!");
            }
            this.existModification = false;
            return true; // Mise � jour effectu�e
        }
        return false; // Mise � jour non effectu�e
    }

    /**
     * @return TRUE si un tag ID3v1 existe dans le fichier, FALSE sinon.
     */
    public boolean existTag() {
        return this.existTag;
    }

    /**
     * Mise � jour de l'indicateur indiquant si le tag va �tre ajout� ou
     * supprim� au prochain appel de la m�thode "recordTag()".
     *
     * @param tagDesactive TRUE pour supprimer le tag, FALSE pour l'ajouter au
     * prochain appel de la m�thode "recordTag()".
     */
    public void setTagDesactive(boolean tagDesactive) {
        if (tagDesactive != this.tagDesactive) {
            this.tagDesactive = tagDesactive;
            this.existModification = true;
        }
    }

    /**
     * Modification du style de la chanson dans l'objet. Voir le tableau des
     * correspondances pour connaitre le code associ� au style.
     * <BR><BR>ATTENTION : Pour que la modification soit prise en compte dans le
     * fichier m�me, il faut appeler la m�thode d'enregistrement "recordTag()".
     *
     * @param genre Le code associ� au style.
     */
    public void setGenre(int genre) {

        if (genre != this.genre) {
            this.genre = genre;
            this.existModification = true;
        }
    }

    /**
     * Modifie la valeur du champ numero de chanson dans l'objet dans le cas o�
     * l'on permet la modification du fichier. Si l'objet a �t� d�fini en
     * lecture seule, cette m�thode est sans effet. <BR><BR>ATTENTION : Pour que
     * la modification soit prise en compte dans le fichier m�me, il faut
     * appeler la m�thode d'enregistrement "recordTag()".
     *
     * @param album Le contenu qu'on souhaite mettre dans le champ album.
     */
    public void setNumChanson(String numChanson) {
        if (!this.modeLectureSeule && !numChanson.equals(this.numChanson)) {
//			if (numChanson.length() > 2){
//				numChanson = numChanson.substring(0, 2);
//			}

            try {
                new Integer(numChanson);
            } catch (NumberFormatException e) {
                numChanson = "0";
            }

            this.numChanson = numChanson;
            this.existModification = true;
        }
    }

    /**
     * Methode qui permet de fermer le tag. Apr�s cette action (irr�m�diable)
     * l'objet devient caduque s'il avait �t� d�fini avec la possibilit� de
     * faire des modifications dans le fichier. En effet cette action met
     * automatiquement l'objet en lecture seule.
     *
     * @throws Id3TagException Exception lev�e s'il est impossible de fermer le
     * tag.
     */
    public void closeTag() throws Id3TagException {
        if (this.fichier != null) {
            try {
                this.fichier.close();
                this.fichier = null;
            } catch (IOException e) {
                throw new Id3TagException(e, "Impossible de fermer l'objet d'acces eu fichier !!");
            }
        }
        this.modeLectureSeule = true;
    }
}
