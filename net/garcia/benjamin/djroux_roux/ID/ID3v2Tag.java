package net.garcia.benjamin.djroux_roux.ID;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ID3v2Tag {

    /**
     * Le fichier sur lequel on cr�e l'objet.
     */
    private RandomAccessFile fichier = null;
    /**
     * La version maximale des tags trait�e (La version maximale actuelle
     * d�finie est 4, mais elle n'est quasiement jamais employ�e).
     */
    public final int VERSION_MAX_TRAITEE = 4;
    /**
     * La version minimale des tags trait�e (La premi�re version sortie est la
     * deux...enfin � ma connaissance).
     */
    public final int VERSION_MIN_TRAITEE = 2;
    /**
     * La r�vision maximale des tags trait�e. A ma connaissance il n'y a jamais
     * eu de r�vision, le champ doit donc toujours valoir z�ro.
     */
    public final int REVISION_MAX_TRAITEE = 0;
    //-------------------------------------------------------
    //----- Les huit variables ci-dessous permettent de -----
    //-----         r�cup�rer un bit d'un octet         -----
    //-------------------------------------------------------
    private final int BIT8 = 0x80;
    private final int BIT7 = 0x40;
    private final int BIT6 = 0x20;
    private final int BIT5 = 0x10;
    private final int BIT4 = 0x08;
    private final int BIT3 = 0x04;
    private final int BIT2 = 0x02;
    private final int BIT1 = 0x01;
    /**
     * Contient la taille du tag.
     */
    private int tagSize = 0;
    /**
     * Contient la version du tag
     */
    private int version = 0;
    /**
     * Contient la r�vision du tag
     */
    private int revision = 0;
    /**
     * Contient le padding du tag.<BR><BR> <U>Rappel :</U> Le padding est le
     * nombre d'octets � 0x00 apr�s la derni�re frame.
     */
    private int padding = 0;
    /**
     * Indique si TRUE qu'il existe une d�synchronisation.
     */
    private boolean unsynchro = false;
    /**
     * Indique si TRUE qu'il existe un header �tendu.
     */
    private boolean extHeader = false;
    /**
     * Indique si TRUE que le tag est exp�riemental.
     */
    private boolean expIndic = false;
    /**
     * Indique si TRUE qu'il existe un footer.
     */
    private boolean footer = false;
    /**
     * Indique si TRUE qu'un tag valide existe.
     */
    private boolean existTag = false;
    /**
     * Indique si TRUE que le tag sera supprim� au prochain enregistrement.
     */
    private boolean tagDesactive = false;
    /**
     * Indique si TRUE que le tag ne peut pas �tre modifi�.
     */
    private boolean modeLectureSeule = false;
    /**
     * Indique si TRUE qu'une modification existe dans l'objet.
     */
    private boolean existModification = false;
    /**
     * Constante Integer contenant la valeur -1.
     */
    private final Integer I_RETOUR_ERREUR = -1;
    /**
     * Contient l'ensemble des frames du tag.
     */
    private ID3v2Frame frames = new ID3v2Frame();
    /**
     * Contient sous forme de tableau de byte le contenu du fichier hormis le
     * tag.
     */
    private byte[] contenuSansTagId3v2 = null;
    /**
     * Contient l'ensemble du tag.
     */
    private byte[] buffer = null;
    /**
     * Taille du fichier en octets
     */
    private long fileLength = 0;
    /**
     * Constante valant z�ro.
     */
    private final byte BYTE_VALEUR_ZERO = 0;
    /**
     * Constante valant un.
     */
    private final byte BYTE_VALEUR_UN = 1;
    /**
     * Les champs commentaire peuvent �tre plusieurs. En revanche, le couple
     * langage / description doit �tre unique. Cet objet contient donc chaque
     * couple sous forme d'un tableau d'objet.
     */
    private ArrayList<Object[]> uniqueIdChampCom = new ArrayList<Object[]>();

    /**
     * Constructeur secondaire qui ne permet pas de choisir l'offset de d�part
     * et qui r�cup�re toujours les frames. Pour plus de d�tails vois le
     * constructeur principal.
     *
     * @param file Le chemin du fichier duquel on veut r�cup�rer les frames.
     * @param mode "r" Pour lecture seule, "rw" pour pouvoir modifier le fichier
     * (Voir mode RandomAccessFile).
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public ID3v2Tag(String file, String mode) throws Id3TagException {
        this(file, mode, 0, true);
    }

    /**
     * Constructeur secondaire qui r�cup�re toujours les frames. Pour plus de
     * d�tails vois le constructeur principal.
     *
     * @param file Le chemin du fichier duquel on veut r�cup�rer les frames.
     * @param mode "r" Pour lecture seule, "rw" pour pouvoir modifier le fichier
     * (Voir mode RandomAccessFile).
     * @param offset L'indice (en octets) � partir duquel on commence �
     * rechercher le tag.
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public ID3v2Tag(String file, String mode, int offset) throws Id3TagException {
        this(file, mode, offset, true);
    }

    /**
     * Constructeur principal de la classe.<BR>
     *
     * Permet de r�cup�rer et de modifier (avec le mode ad�quat) les
     * informations d'un tag ID3v2.<BR><BR> <U>NOTE :</U> Compte tenu de la
     * richesse potentielle des tags ID3v2, cette classe ne traite qu'un petite
     * partie des donn�es possibles. Si une frame non prise en charge est
     * trouv�e, elle est ignor�e en lecture, et r��crite telle quelle � la
     * modification du tag.<BR><BR>
     *
     * <B>Infos sur les param�tres</B><BR><BR> Le deuxi�me param�tre permet de
     * choisir si le tag est modifiable ou pas.<BR> Le troisi�me param�tre
     * permet de d�marrer la recherche du tag dans une autre position que z�ro.
     * Cette possiblit� est donn�e pour pouvoir chercher l'existence de
     * plusieurs tags les uns derri�re les autres. En effet, si une application
     * ne sait pas modifier un tag, elle r��crit le sien juste devant.
     * Cons�quence, pour trouver le d�but du MP3, il faut savoir s'il existe
     * plusieurs tags ou pas.<BR> Le quatri�me param�tre permet de ne pas
     * r�cup�rer les frames. Il est fortement associ� au param�tre pr�cedent car
     * si on veut rechercher l'existence de plusieurs tags, la r�cup�ration des
     * frames n'est pas n�cessaire. Cela permet alors de gagner pas mal en terme
     * de performances.
     *
     * @param file Le chemin du fichier duquel on veut r�cup�rer les frames.
     * @param mode "r" Pour lecture seule, "rw" pour pouvoir modifier le fichier
     * (Voir mode RandomAccessFile).
     * @param offset L'indice (en octets) � partir duquel on commence �
     * rechercher le tag.
     * @param recupFrames Si false, on ne regarde que l'ent�te du tag. Si TRUE,
     * on r�cup�re aussi les frames.
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public ID3v2Tag(String file, String mode, int offset, boolean recupFrames) throws Id3TagException {

        try {
            this.fichier = new RandomAccessFile(file, mode);
        } catch (FileNotFoundException e) {
            throw new Id3TagException(e, "Impossible de cr�er l'objet d'acc�s au fichier !!");
        }

        try {
            fileLength = this.fichier.length();
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de r�cup�rer la taille du fichier !!");
        }

        if (mode.equals("r")) {
            this.modeLectureSeule = true;
        } else {
            // Si la taille du fichier excede 1 Go
            if (fileLength > 1073741824) {
                throw new Id3TagException("Tailles de fichier > 1 Go non pris en charge dans le mode lecture/�criture !!");
            }
        }

        if (!this.existID3(this.fichier, offset)) {
            this.existTag = false;

            if (!this.modeLectureSeule) {

                this.contenuSansTagId3v2 = new byte[((int) fileLength) - offset];

                try {
                    this.fichier.seek(offset);
                    this.fichier.read(this.contenuSansTagId3v2);
                } catch (IOException e) {
                    throw new Id3TagException(e, "Impossible de lire le contenu du fichier");
                }
            }
            return;
        }

        this.existTag = true;
        this.version = this.majorVersion(offset);
        this.revision = this.revision(offset);

        boolean[] retour = this.flags(offset);

        this.unsynchro = retour[0];

        if (this.unsynchro) {
            // TODO Faire traitement en cas de non synchronisation.
            throw new Id3TagException("Non synchronisation pr�sente et non trait�e actuellement fin !!");
        }

        this.extHeader = retour[1];

        if (this.extHeader) {
            // TODO Faire traitement en cas d'header �tendu.
            throw new Id3TagException("Header �tendu pr�sent et non trait�e actuellement !!");
        }

        this.expIndic = retour[2];

        if (this.expIndic) {
            // TODO Faire traitement en cas de tag exp�riemental
            throw new Id3TagException("Tag exp�riemental pr�sent et non trait�e actuellement !!");
        }

        this.footer = retour[3];

        if (this.footer) {
            // TODO Faire traitement en cas de footer
            throw new Id3TagException("Footer pr�sent et non trait�e actuellement !!");
        }

        this.setTagSize(offset);

        this.buffer = new byte[this.tagSize];

        try {
            this.fichier.seek(offset);
            this.fichier.read(this.buffer);
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de lire le contenu du fichier apr�s le tag");
        }

        if (!this.modeLectureSeule) {

            this.contenuSansTagId3v2 = new byte[((int) fileLength - this.tagSize - offset)];

            try {
                this.fichier.seek(this.tagSize + offset);
                this.fichier.readFully(this.contenuSansTagId3v2);
            } catch (IOException e) {
                throw new Id3TagException(e, "Impossible de lire le contenu du fichier apr�s le tag");
            }
        }

        if (recupFrames) {
            if (((this.version == 3 || this.version == 4) && this.revision == 0)
                    || (this.version == 2 && this.revision == 0)) {
                this.getFrames(offset);
            } else {
                throw new Id3TagException("La version n�" + this.version
                        + " et/ou la r�vision n�" + this.revision + " n'est pas g�r�e !!");
            }
        }
    }

    /**
     * Cette m�thode permet de d�tecter s'il existe ou pas un tag id3v2.<br><br>
     * NOTE : Cette m�thode peut �tre perfectionn�e. En effet cette m�thode ne
     * renseigne pas du tout sur la validit� du tag.<br>
     *
     * @param file Le fichier dont on cherche � conna�tre la pr�sence d'un tag
     * id3v2.
     * @param offset dans le cas o� il existe plusieurs tags ID3v2, permet de
     * donner une nouvelle adresse de depart.
     * @return "true" si un tag existe, "false" sinon.
     * @throws Id3TagException L'exception lev�e en cas d'erreur d'acc�s au
     * fichier.
     */
    private boolean existID3(RandomAccessFile file, int offset) throws Id3TagException {

        final byte[] buffer = new byte[3];

        // La longueur du fichier doit �tre d'au moins 10 caract�res pour contenir un tag id3v2
        try {
            if (file.length() < 10) {
                throw new Id3TagException("Fichier pas assez long pour contenir un tag ID3v2");
            }
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de lire le contenu du fichier");
        }

        // Recherche du mot "ID3" qui introduit les tags
        try {
            file.seek(offset);
            file.read(buffer, 0, 3);
        } catch (IOException e) {
            throw new Id3TagException(e,
                    "Impossible de lire le mot \"ID3\" qui pr�c�de le header (Tag incorrect ou absent)");
        }

        return new String(buffer, 0, 3).equals("ID3");
    }

    /**
     * Cette m�thode permet de conna�tre la version du tag id3v2 entrain d'�tre
     * scann�.<br><br>
     *
     * ATTENTION : On ne parle ici des tags id3v1 et id3v2, mais bien de la
     * version utilis� pour le tag id3v2. Il existe actuellement trois versions
     * : id3v2_2, id3v2_3 et id3v2_4.<br>
     *
     * @param file Le fichier dont on cherche � conna�tre la pr�sence d'un tag
     * id3v2.
     * @return Le num�ro de version utilis� par le tag id3v2
     * @throws Id3TagException L'exception lev�e en cas d'erreur d'acc�s au
     * fichier ou si la version r�cup�r�e est incorrecte
     */
    private byte majorVersion(int offset) throws Id3TagException {

        byte[] buffer = new byte[1];

        // Recherche de l'octet qui donne la version majeure du tag
        try {
            this.fichier.seek(3 + offset);
            this.fichier.read(buffer, 0, 1);
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de lire l'octet qui donne la version majeure du tag");
        }

        // La version majeure ne peut pas �tre 255
        if (buffer[0] == 0xFF) {
            throw new Id3TagException("Version majeure du tag incorrecte");
        }

        if (buffer[0] > VERSION_MAX_TRAITEE || buffer[0] < VERSION_MIN_TRAITEE) {
            throw new Id3TagException("Version majeure du tag non pris en charge");
        }

        return buffer[0];
    }

    /**
     * Cette m�thode permet de conna�tre la r�vision de la version du tag id3v2
     * entrain d'�tre scann�.<br>
     *
     * @param file Le fichier dont on cherche � conna�tre la pr�sence d'un tag
     * id3v2.
     * @return Le num�ro de version utilis� par le tag id3v2
     * @throws Id3TagException L'exception lev�e en cas d'erreur d'acc�s au
     * fichier ou si la version r�cup�r�e est incorrecte
     */
    private byte revision(int offset) throws Id3TagException {

        byte[] buffer = new byte[1];

        // Recherche de l'octet qui donne la version majeure du tag
        try {
            this.fichier.seek(4 + offset);
            this.fichier.read(buffer, 0, 1);
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de lire l'octet qui donne la r�vision du tag");
        }

        // La r�vision ne peut pas �tre 255
        if (buffer[0] == 0xFF) {
            throw new Id3TagException("R�vision du tag incorrecte");
        }

        if (buffer[0] > REVISION_MAX_TRAITEE) {
            throw new Id3TagException("R�vision du tag non pris en charge");
        }

        return buffer[0];
    }

    /**
     * Cette m�thode r�cup�re les flags du header du tag id3v2 et retourne un
     * tableau de 4 bool�ens.<br><br>
     *
     * Actuellement les tags id3v2 utilisent 4 flags : <br> <dd><dd>a -
     * Unsynchronisation : indique si oui ou non la non synchronisation est
     * utilis�e sur toutes les frames. Ce flags est contenu dans le champ 0 du
     * tableau de bool�ens retourn� par la m�thode.<br> <dd><dd>b - Extended
     * header : indique s'il existe un header �tendu. Ce flags est contenu dans
     * le champ 1 du tableau de bool�ens retourn� par la m�thode.<br> <dd><dd>c
     * - Experimental indicator : Indique si le tag est en cours de
     * d�veloppement. Ce flags est contenu dans le champ 2 du tableau de
     * bool�ens retourn� par la m�thode.<br> <dd><dd>d - Footer present :
     * indique si un footer est pr�sent � la fin du tag id3v2. Ce flags est
     * contenu dans le champ 3 du tableau de bool�ens retourn� par la
     * m�thode.<br><br>
     *
     * @param file Le fichier dont on cherche � conna�tre la pr�sence d'un tag
     * id3v2.
     * @return un tableau de bool�ens contenant les indicateurs de flags
     * @throws Id3TagException L'exception lev�e en cas d'erreur d'acc�s au
     * fichier ou si l'octet qui contient les flags est incorrect.
     */
    private boolean[] flags(int offset) throws Id3TagException {

        final byte[] buffer = new byte[1];

        boolean[] retour = new boolean[4];

        try {
            this.fichier.seek(5 + offset);
            this.fichier.read(buffer, 0, 1);
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de lire l'octet qui contient les flags");
        }

        //On r�cup�re le bit de poids fort
        if ((buffer[0] & BIT8) != 0) {
            retour[0] = true;
        }

        if ((buffer[0] & BIT7) != 0) {
            retour[1] = true;
        }

        if ((buffer[0] & BIT6) != 0) {
            retour[2] = true;
        }

        if ((buffer[0] & BIT5) != 0) {
            retour[3] = true;
        }

        if ((buffer[0] & BIT4) != 0 || (buffer[0] & BIT3) != 0
                || (buffer[0] & BIT2) != 0 || (buffer[0] & BIT1) != 0) {
            throw new Id3TagException("L'octet qui contient les flags est incorrect "
                    + "(au moins des quatre bits de poids faible n'est pas nul)");
        }

        return retour;
    }

    /**
     * Cette m�thode retourne la taille totale du tag id3v2.<br><br> NOTE : La
     * taille totale tient compte du header et du footer s'il est pr�sent.<br>
     *
     * @return La taille totale du tag en octets.
     * @throws Id3TagException L'exception lev�e en cas d'erreur d'acc�s au
     * fichier ou si la taille du tag est incorrecte.
     */
    private void setTagSize(int offset) throws Id3TagException {

        byte[] buffer = new byte[4];

        int size = 0;

        try {
            this.fichier.seek(6 + offset);
            this.fichier.read(buffer, 0, 4);
        } catch (IOException e) {
            throw new Id3TagException(e, "Impossible de lire les octets qui donnent la taille du tag");
        }

        if (buffer[0] > 0x80 || buffer[1] > 0x80 || buffer[1] > 0x80 || buffer[1] > 0x80) {
            throw new Id3TagException("La taille du tag est incorrecte");
        }

        // On fait des d�calages � gauche pour supprimer  les bits inutiles	et on ajoute
        // 10 pour le header qui n'est pas pris en compte.s
        size = (buffer[0] << 21) + (buffer[1] << 14) + (buffer[2] << 7) + buffer[3] + 10;

        if (this.footer) {
            size += 10;
        }

        this.tagSize = size;
    }

    /**
     * Met en m�moire les frames d'un tag de version 3 et de r�vision 0
     *
     * @param offset Indice � partir duquel on doit chercher les frames.
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    private void getFrames(int offset) throws Id3TagException {

        int realOffset = 10 + offset;
        int compteur = 0;

        Byte[] flags = null;

        while (realOffset < this.tagSize) {

            String frameId = null;
            String content = null;
            String description = null;
            String langage = null;

            int size = 0;
            int sizeToRecord = 0;
            int typeContenu = 0;
            int lenFrameId = 0;
            int headerSize = 0;

            boolean ignorerFrame = false; // Car on ne sait pas la traiter...

            Byte encodingByte = this.BYTE_VALEUR_ZERO;

            if (this.version == 2) {
                lenFrameId = 3;
                headerSize = 6;
            } else {
                lenFrameId = 4;
                headerSize = 10;
            }

            if (this.buffer.length < realOffset + lenFrameId) {

                byte[] nullFrame = new byte[realOffset + lenFrameId - this.buffer.length];

                for (int i = 0; i < nullFrame.length; i++) {
                    if (this.buffer[realOffset + i] != 0x00) {
                        throw new Id3TagException("Il n'y a pas que des octets nuls dans le padding");
                    }
                }

                this.padding = nullFrame.length;
                return;
            }

            // R�cup�ration du nom du tag
            frameId = new String(this.buffer, realOffset, lenFrameId);


            // On v�rifie avec une expression r�guli�re que l'identifiant de la frame r�cup�r� n'est compos�
            // que de lettres majuscules ou de chiffres.
            Pattern pattern = Pattern.compile("[A-Z0-9]{" + lenFrameId + "}");
            Matcher matcher = pattern.matcher(frameId);

            if (!matcher.find()) {

                byte[] nullFrame = new byte[lenFrameId];

                for (int i = 0; i < lenFrameId; i++) {
                    nullFrame[i] = 0x00;
                }

                // Si si l'identifiant de la frame vaut {0x00, 0x00, 0x00, 0x00}, � priori c'est le d�but du padding.
                if (frameId.equals(new String(nullFrame))) {

                    this.padding = 0;
                    // On v�rifie donc que c'est bien le cas
                    for (int i = realOffset; i < this.tagSize; i++) {
                        if (this.buffer[i] != 0x00) {
                            throw new Id3TagException("Il n'y a pas que des octets nuls dans le padding");
                        }
                        this.padding++;
                    }
                    // S'il n'y a que des z�ros, on �tait bien sur le padding 
                    //    => on a parcouru toutes les frames
                    //       => on sort.
                    return;
                }

                if (frameId.substring(0, 3).equals("CM1")) {
                    return;
                }

                throw new Id3TagException("L'identifiant de la frame (" + frameId + ") est incorrect !");
            }

            typeContenu = frames.getFrameType(frameId);

            if (typeContenu == -1) {
                throw new Id3TagException("La frame est exp�rimentale");
            }

            if (typeContenu == -2) {
                throw new Id3TagException("Type de frame non pris en charge : " + frameId);
            }

            // Une diff�rence notoire entre les versions 3 et 4 est la fa�on dont on calcule la
            // taille des frames. En version 3 on utilise des integer normaux alors qu'en version
            // 4 on utilise des synchsafe integer.
            if (this.version == 4) {
                size = ((this.buffer[realOffset + 4] & 0x7f) << 21)
                        | ((this.buffer[realOffset + 5] & 0x7f) << 14)
                        | ((this.buffer[realOffset + 6] & 0x7f) << 7)
                        | ((this.buffer[realOffset + 7] & 0x7f) << 0);
            } else if (this.version == 3) {
                size = ((this.buffer[realOffset + 4] & 0xff) << 24)
                        | ((this.buffer[realOffset + 5] & 0xff) << 16)
                        | ((this.buffer[realOffset + 6] & 0xff) << 8)
                        | ((this.buffer[realOffset + 7] & 0xff) << 0);
            } else {
                size = ((this.buffer[realOffset + 3] & 0xff) << 16)
                        | ((this.buffer[realOffset + 4] & 0xff) << 8)
                        | ((this.buffer[realOffset + 5] & 0xff) << 0);
            }


            if (size > this.tagSize) {
                throw new Id3TagException("La taille de la frame ne pas d�passer celle du tag");
            }

            sizeToRecord = size;

            if (this.version == 3 || this.version == 4) {
                flags = new Byte[2];

                flags[0] = new Byte(this.buffer[realOffset + 8]);
                flags[1] = new Byte(this.buffer[realOffset + 9]);
            }

            switch (typeContenu) {
                case ID3v2Frame.TEXT_INFORMATION_FRAME: {
                    //$00 ISO-8859-1 [ISO-8859-1]. Terminated with $00.
                    //$01 UTF-16 [UTF-16] encoded Unicode [UNICODE] with BOM. 
                    //    All strings in the same frame SHALL have the same byteorder. Terminated with $00 00.
                    //$02 UTF-16BE [UTF-16] encoded Unicode [UNICODE] without BOM. Terminated with $00 00.
                    //$03 UTF-8 [UTF-8] encoded Unicode [UNICODE]. Terminated with $00.
                    String encoding = "";

                    switch (this.buffer[realOffset + headerSize]) {
                        case 0x00:
                            encoding = "ISO-8859-1";
                            break;
                        case 0x01:
                            encoding = "UTF-16";
                            break;
                        case 0x02:
                            encoding = "UTF-16";
                            break;
                        case 0x03:
                            encoding = "UTF-8";
                            break;
                        default:
                            throw new Id3TagException("Encodage non pris en charge");
                    }

                    encodingByte = new Byte(this.buffer[realOffset + headerSize]);

                    int tailleContenu = 0;

                    if (size > 1) {
                        // R�cup�ration des deux derniers octets de la frame
                        if (this.buffer[realOffset + headerSize + size - 1] == 0x00 && encodingByte.byteValue() == 0x00) {
                            if (this.buffer[realOffset + headerSize + size - 2] == 0x00 && encodingByte.byteValue() == 0x01) {
                                tailleContenu = size - 3;
                                sizeToRecord -= 2;
                            } else {
                                tailleContenu = size - 2;
                                sizeToRecord -= 1;
                            }
                        } else {
                            tailleContenu = size - 1;
                        }
                    }

                    if (tailleContenu <= 0) {
                        content = "";
                    } else {
                        try {
                            content = new String(this.buffer, realOffset + headerSize + 1, tailleContenu, encoding);
                        } catch (UnsupportedEncodingException e) {
                            throw new Id3TagException(e, "Encodage non pris en charge");
                        }
                    }


                    break;
                }
                case ID3v2Frame.COMMENT_FRAME: {
                    //$00 ISO-8859-1 [ISO-8859-1]. Terminated with $00.
                    //$01 UTF-16 [UTF-16] encoded Unicode [UNICODE] with BOM. 
                    //    All strings in the same frame SHALL have the same byteorder. Terminated with $00 00.
                    //$02 UTF-16BE [UTF-16] encoded Unicode [UNICODE] without BOM. Terminated with $00 00.
                    //$03 UTF-8 [UTF-8] encoded Unicode [UNICODE]. Terminated with $00.
                    String encoding = "";

                    int nbZeros = 0;

                    switch (this.buffer[realOffset + headerSize]) {
                        case 0x00:
                            encoding = "ISO-8859-1";
                            nbZeros = 1;
                            break;
                        case 0x01:
                            encoding = "UTF-16";
                            nbZeros = 2;
                            break;
                        case 0x02:
                            encoding = "UTF-16";
                            nbZeros = 2;
                            break;
                        case 0x03:
                            encoding = "UTF-8";
                            nbZeros = 1;
                            break;
                        default:
                            throw new Id3TagException("Encodage non pris en charge");
                    }

                    encodingByte = new Byte(this.buffer[realOffset + headerSize]);

                    // R�cup�ration du contenu de la frame
                    try {
                        langage = new String(this.buffer, realOffset + headerSize + 1, 3);

                        for (int i = 0; i < size - 4; i++) {
                            // Si la condition ci-dessous est v�rifi�e, on a atteind le s�parateur
                            if (this.buffer[i + realOffset + headerSize + 4] == 0x00) {
                                if (nbZeros == 1 || (nbZeros == 2 && this.buffer[i + realOffset + headerSize + 5] == 0x00)) {
                                    if (i != 0) {
                                        // Dans ce cas, on a simplement les deux octets 0xFFFE ou 0xFEFF qui
                                        // d�marrent la description. on n'en tient pas compte lors de l'enregistrement
                                        if (i == 2 && encodingByte.byteValue() == 0x01) {
                                            sizeToRecord -= 2;
                                        }

                                        description = new String(this.buffer, realOffset + headerSize + 4, i, encoding);
                                    } else {
                                        description = "";
                                    }

                                    int nbTerminatingZeros = 0;

                                    // Le champ poss�de toujours une langue (3 octets), un encodage (1 octet) et
                                    // le s�parateur entre la description et le contenu (1 ou 2 octet(s))
                                    if (size > (4 + nbZeros)) {
                                        // R�cup�ration des deux derniers octets de la frame
                                        if (this.buffer[realOffset + headerSize + size - 1] == 0x00
                                                && encodingByte.byteValue() == 0x00) {
                                            if (this.buffer[realOffset + headerSize + size - 2] == 0x00
                                                    && encodingByte.byteValue() == 0x01) {
                                                nbTerminatingZeros = 2;
                                                sizeToRecord -= 2;
                                            } else {
                                                nbTerminatingZeros = 1;
                                                sizeToRecord -= 1;
                                            }
                                        }
                                    }

                                    if (size - i - 4 - nbZeros - nbTerminatingZeros <= 0) {
                                        content = "";
                                    } else {
                                        content = new String(this.buffer,
                                                realOffset + headerSize + 4 + i + nbZeros,
                                                size - i - 4 - nbZeros - nbTerminatingZeros,
                                                encoding);
                                    }
                                    break;
                                }
                            }

                            // Cas normalement impossible car on a forcement le s�parateur qui est pr�sent.
                            if (i == size - 5 && this.buffer[i + realOffset + headerSize + 4] != 0x00) {
                                content = new String(this.buffer, realOffset + headerSize + 4, size - 4, encoding);
                                description = "";
                                break;
                            }
                        }

                        for (int i = 0; i < this.uniqueIdChampCom.size(); i++) {

                            Object[] retour = this.uniqueIdChampCom.get(i);

                            if (retour[0].equals(langage) && retour[1].equals(description)) {
                                throw new Id3TagException("Il ne peut exister qu'une seule frame avec le couple ("
                                        + langage + "/" + description + ")");
                            }
                        }

                        this.uniqueIdChampCom.add(new Object[]{langage, description});
                    } catch (UnsupportedEncodingException e) {
                        throw new Id3TagException(e, "Impossible d'encoder correctement les chaines de caract�res");
                    }

                    break;
                }
                case ID3v2Frame.URL_LINK_FRAME_WXXX: {
                    // $00 ISO-8859-1 [ISO-8859-1]. Terminated with $00.
                    // $01 UTF-16 [UTF-16] encoded Unicode [UNICODE] with BOM. 
                    //     All strings in the same frame SHALL have the same byteorder. Terminated with $00 00.
                    // $02 UTF-16BE [UTF-16] encoded Unicode [UNICODE] without BOM. Terminated with $00 00.
                    // $03 UTF-8 [UTF-8] encoded Unicode [UNICODE]. Terminated with $00.
                    String encoding = "";

                    int nbZeros = 0;

                    switch (this.buffer[realOffset + headerSize]) {
                        case 0x00:
                            encoding = "ISO-8859-1";
                            nbZeros = 1;
                            break;
                        case 0x01:
                            encoding = "UTF-16";
                            nbZeros = 2;
                            break;
                        case 0x02:
                            encoding = "UTF-16";
                            nbZeros = 2;
                            break;
                        case 0x03:
                            encoding = "UTF-8";
                            nbZeros = 1;
                            break;
                        default:
                            throw new Id3TagException("Encodage non pris en charge");
                    }

                    encodingByte = new Byte(this.buffer[realOffset + headerSize]);

                    // R�cup�ration du contenu de la frame
                    try {
                        for (int i = 0; i < size - 1; i++) {
                            // Si la condition ci-dessous est v�rifi�e, on a atteind le s�parateur
                            if (this.buffer[i + realOffset + headerSize + 1] == 0x00) {
                                if (nbZeros == 1 || (nbZeros == 2 && this.buffer[i + realOffset + headerSize + 2] == 0x00)) {
                                    if (i != 0) {
                                        description = new String(this.buffer, realOffset + headerSize + 1, i, encoding);
                                    } else {
                                        description = "";
                                    }

                                    content = new String(this.buffer,
                                            realOffset + headerSize + 1 + i + nbZeros,
                                            size - i - 1 - nbZeros,
                                            encoding);
                                    break;
                                }
                            }

                            // Cas normalement impossible car on a forcement le s�parateur qui est pr�sent.
                            if (i == size - 2 && this.buffer[i + realOffset + headerSize + 1] != 0x00) {
                                content = new String(this.buffer, realOffset + headerSize + 1, size - 1, encoding);
                                description = "";
                                break;
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                        throw new Id3TagException(e, "Impossible d'encoder correctement les chaines de caract�res");
                    }

                    break;
                }
                case ID3v2Frame.MUSIC_CD_IDENTIFIER_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.UNIQUE_FILE_IDENTIFIER_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.URL_LINK_FRAME: {
                    // TODO Prendre en charge ce type de donn�e
                    ;
                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.POPULARIMETER_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.PRIVATE_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.USER_DEFINED_TEXT_INFORMATION_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.PLAY_COUNTER_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.GENERAL_ENCAPSULATED_OBJECT_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.ATTACHED_PICTURE_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                case ID3v2Frame.UNSYNCHRONISED_LYRICS_FRAME: {
                    // TODO Prendre en charge ce type de donn�e

                    ignorerFrame = true;
                    break;
                }
                default:
                    // TODO Prendre en charge tous les types de frame.
                    throw new Id3TagException("Type de frame non pris en charge : " + frameId);
            }

            size = size + headerSize;
            sizeToRecord += headerSize;



            // On part du principe que si on ne sait pas g�rer un type de frame, autant l'ignorer.
            // Seul d�faut, � la mise � jour du tag, les frames non g�r�es seront supprim�es.
            if (!ignorerFrame) {
                frames.addFrame(frameId, new Integer(sizeToRecord), flags, content, description, encodingByte, langage);
            } else {
                Byte[] frameIgnoree = new Byte[size];
                for (int i = 0; i < size; i++) {

                    frameIgnoree[i] = new Byte(this.buffer[realOffset + i]);
                }
                frames.addIgnoredFrame(frameIgnoree);
            }

            compteur++;
            realOffset = realOffset + size;
        }
    }

    //----------------------------------------------------------
    //---------- M�thodes d'acc�s (getters / setters) ----------
    //----------------------------------------------------------    
    /**
     * @return Le nom d'artiste contenu dans le tag id3v2 du fichier.
     */
    public String getArtiste() {
        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TP1");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TPE1");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ artiste du tag dans l'objet.<BR><BR> <U>ATTENTION :</U>
     * Pour que la modification soit prise en compte dans le fichier m�me, il
     * faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param artiste La chaine de caract�res � enregistrer.
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setArtiste(String artiste) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TP1", artiste);
        } else {
            this.setTextFrame("TPE1", artiste);
        }
    }

    /**
     * @return Le titre contenu dans le tag id3v2 du fichier.
     */
    public String getTitre() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TT2");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TIT2");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ titre du tag dans l'objet.<BR><BR> <U>ATTENTION :</U>
     * Pour que la modification soit prise en compte dans le fichier m�me, il
     * faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param titre La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setTitre(String titre) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TT2", titre);
        } else {
            this.setTextFrame("TIT2", titre);
        }
    }

    /**
     * @return L'album contenu dans le tag id3v2 du fichier.
     */
    public String getAlbum() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TAL");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TALB");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ album du tag dans l'objet.<BR><BR> <U>ATTENTION :</U>
     * Pour que la modification soit prise en compte dans le fichier m�me, il
     * faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param album La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setAlbum(String album) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TAL", album);
        } else {
            this.setTextFrame("TALB", album);
        }
    }

    /**
     * @return Le commentaire contenu dans le tag id3v2 du fichier.
     */
    public String getCommentaire(int indice) {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("COM", indice);
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("COMM", indice);
        }

        return temp;
    }

    /**
     * Modifie le champ commentaire d'occurence "indice" dans l'objet.<BR><BR>
     * <U>ATTENTION :</U> Pour que la modification soit prise en compte dans le
     * fichier m�me, il faut appeler la m�thode d'enregistrement
     * "recordTag()".<BR><BR>
     *
     * @param langue La langue dans laquelle on �crit le commentaire.
     * @param description La description du commentaire.
     * @param commentaire La chaine de caract�res � enregistrer.
     * @param indice L'occurence de la frame (Plusieurs frames peuvent avoir le
     * m�me identifiant).
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setDatasCommentaire(String langue, String description, String commentaire, int indice) throws Id3TagException {

        // Un tag existe...
        if (this.existTag) {
            // Ce tag est en version 2...
            if (this.version == 2) {
                // On n'a pas saisi d'artiste...                
                if (commentaire == null || commentaire.length() == 0) {
                    this.frames.removeFrame("COM", indice);
                } // On a saisi un artiste...
                else {
                    // On a saisi un artiste diff�rent de celui d�j� en m�moire...
                    if (this.frames.existFrame("COM", indice)) {
                        // La frame existait d�j�...on la met � jour
                        if (!frames.getFrameContent("COM", indice).equals(commentaire)
                                || !frames.getFrameDescription("COM", indice).equals(description)
                                || !frames.getFrameLanguage("COM", indice).equals(langue)) {

                            this.frames.setFrame("COM", langue, description, commentaire, indice);
                            this.existModification = true;
                        }
                    } // La frame n'existait pas...on la cr�e.
                    else {
                        // Si la langue n'est pas pr�cis�e, on met fre pour France
                        if (langue == null || langue.equals("")) {
                            langue = "fre";
                        }

                        if (description == null) {
                            description = "";
                        }

                        // On encode par d�faut en "UTF-16", if faut donc ajouter 2 � la taille carle s�parateur
                        // fait deux z�ros.
                        try {
                            int tailleFrame = 10 + // header + langage + encoding
                                    description.getBytes("UTF-16").length
                                    + commentaire.getBytes("UTF-16").length
                                    + 2; // Le s�parateur sur deux octets car on est en UTF-16

                            this.frames.addCommentFrameV2("COM", new Integer(tailleFrame), commentaire, description,
                                    new Byte(this.BYTE_VALEUR_UN), langue);
                        } catch (UnsupportedEncodingException e) {
                            throw new Id3TagException(e, "Impossible d'ajouter une frame (Probl�me d'encodage)");
                        }
                    }
                }
            } // On est en version 3...
            else if (this.version == 3 || this.version == 4) {
                // On n'a pas saisi d'artiste...                
                if (commentaire == null || commentaire.length() == 0) {
                    this.frames.removeFrame("COMM", indice);
                } // On a saisi un artiste...
                else {
                    // On a saisi un artiste diff�rent de celui d�j� en m�moire...
                    if (this.frames.existFrame("COMM", indice)) {
                        // La frame existait d�j�...on la met � jour
                        if (!frames.getFrameContent("COMM", indice).equals(commentaire)
                                || !frames.getFrameDescription("COMM", indice).equals(description)
                                || !frames.getFrameLanguage("COMM", indice).equals(langue)) {

                            this.frames.setFrame("COMM", langue, description, commentaire, indice);
                            this.existModification = true;
                        }
                    } // La frame n'existait pas...on la cr�e.
                    else {
                        // Si la langue n'est pas pr�cis�e, on met fre pour France
                        if (langue == null || langue.equals("")) {
                            langue = "fre";
                        }

                        if (description == null) {
                            description = "";
                        }

                        // On encode par d�faut en "UTF-16", if faut donc ajouter 2 � la taille carle s�parateur
                        // fait deux z�ros.
                        try {
                            int tailleFrame = 14 + // header + langage + encoding
                                    description.getBytes("UTF-16").length
                                    + commentaire.getBytes("UTF-16").length
                                    + 2; // Le s�parateur sur deux octets car on est en UTF-16

                            this.frames.addCommentFrameV3("COMM", new Integer(tailleFrame),
                                    new Byte[]{this.BYTE_VALEUR_ZERO, this.BYTE_VALEUR_ZERO},
                                    commentaire, description, new Byte(this.BYTE_VALEUR_UN),
                                    langue);
                        } catch (UnsupportedEncodingException e) {
                            throw new Id3TagException(e, "Impossible d'ajouter une frame (Probl�me d'encodage)");
                        }
                    }
                }
            }
        } // Si aucun tag n'existe, on prend toujours la version 3 des tags ID3v2 pour en cr�er un nouveau.
        else {
            // Si la langue n'est pas pr�cis�e, on met fre pour France
            if (langue == null || langue.equals("")) {
                langue = "fre";
            }

            if (description == null) {
                description = "";
            }

            if (commentaire.length() > 0) {
                // On encode par d�faut en "UTF-16", if faut donc ajouter 2 � la taille carle s�parateur
                // fait deux z�ros.
                try {
                    int tailleFrame = 14 + // header + langage + encoding
                            description.getBytes("UTF-16").length
                            + commentaire.getBytes("UTF-16").length
                            + 2; // Le s�parateur sur deux octets car on est en UTF-16

                    this.frames.addCommentFrameV3("COMM", new Integer(tailleFrame),
                            new Byte[]{this.BYTE_VALEUR_ZERO, this.BYTE_VALEUR_ZERO},
                            commentaire, description, new Byte(this.BYTE_VALEUR_UN), langue);
                } catch (UnsupportedEncodingException e) {
                    throw new Id3TagException(e, "Impossible d'ajouter une frame (Probl�me d'encodage)");
                }
                this.existModification = true;
                this.version = 3;
            }
        }
    }

    /**
     * Modifie le champ commentaire d'occurence "indice" dans l'objet.<BR><BR>
     * <U>ATTENTION :</U> Pour que la modification soit prise en compte dans le
     * fichier m�me, il faut appeler la m�thode d'enregistrement
     * "recordTag()".<BR><BR>
     *
     * @param commentaire La chaine de caract�res � enregistrer.
     * @param indice L'occurence de la frame (Plusieurs frames peuvent avoir le
     * m�me identifiant).
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setCommentaire(String commentaire, int indice) throws Id3TagException {
        this.setDatasCommentaire(null, null, commentaire, indice);
    }

    /**
     * Modifie le champ commentaire d'occurence "indice" dans l'objet.<BR><BR>
     * <U>ATTENTION :</U> Pour que la modification soit prise en compte dans le
     * fichier m�me, il faut appeler la m�thode d'enregistrement
     * "recordTag()".<BR><BR>
     *
     * @param langue La langue du commentaire
     * @param description La description du commentaire.
     * @param commentaire La chaine de caract�res � enregistrer.
     * @param indice L'occurence de la frame (Plusieurs frames peuvent avoir le
     * m�me identifiant).
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setCommentaire(String langue, String description, String commentaire, int indice) throws Id3TagException {
        this.setDatasCommentaire(langue, description, commentaire, indice);
    }

    /**
     * M�thode permettant d'ajouter un flot de frames commentaires en une seule
     * fois. Elle est surtout utile quand on a plusieurs champs
     * commentaire.<BR><BR>
     *
     * @param donnees {langue, description, comentaire} * le nombre de champs
     * @param supprimerReste Si TRUE, s'il existe plus de frames que le nombre
     * pass� en param�tre, alors on supprime celle(s) qui reste(nt).
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setDatasCommentaire(String[][] donnees, boolean supprimerReste) throws Id3TagException {

        int oldNbChampsCommentaire = this.getNbChampsCommentaires();

        for (int i = 0; i < donnees.length; i++) {
            this.setDatasCommentaire(donnees[i][0], donnees[i][1], donnees[i][2], i);
        }

        if (supprimerReste) {
            for (int i = donnees.length; i < oldNbChampsCommentaire; i++) {
                if (this.version == 2) {
                    this.frames.removeFrame("COM", i);
                } else if (this.version == 3 || this.version == 4) {
                    this.frames.removeFrame("COMM", i);
                }
            }
        }
    }

    /**
     * @return L'ann�e contenue dans le tag id3v2 du fichier sous forme de
     * String.
     */
    public String getAnnee() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TYE");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TYER");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ annee dans l'objet.<BR><BR> <U>ATTENTION :</U> Pour que
     * la modification soit prise en compte dans le fichier m�me, il faut
     * appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param annee La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setAnnee(String annee) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TYE", annee);
        } else {
            this.setTextFrame("TYER", annee);
        }
    }

    /**
     * @return Le num�ro de chanson contenu dans le tag id3v2 du fichier.
     */
    public String getNumChanson() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TRK");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TRCK");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ numChanson dans l'objet.<BR><BR> <U>ATTENTION :</U> Pour
     * que la modification soit prise en compte dans le fichier m�me, il faut
     * appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param numChanson La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setNumChanson(String numChanson) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TRK", numChanson);
        } else {
            this.setTextFrame("TRCK", numChanson);
        }
    }

    /**
     * @return Le genre contenu dans le tag id3v2 du fichier sous forme
     * d'integer. Voir tableau des correspondances.
     */
    public int getGenreInteger() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TCO");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TCON");
        }

        Integer retour = -1;
        int emplacementParenthese = 0;

        if (temp != null && temp != "") {

            for (int i = 0; i < temp.length(); i++) {
                if (temp.substring(i, i + 1).equals(")")) {
                    emplacementParenthese = i;
                    break;
                }
            }
            if (emplacementParenthese != 0) {
                retour = new Integer(temp.substring(1, emplacementParenthese));
            } else {
                retour = this.I_RETOUR_ERREUR;
            }
        } else {
            retour = this.I_RETOUR_ERREUR;
        }

        return retour.intValue();
    }

    /**
     * @return Le style de la chanson telle qu'elle est contenu dans la frame.
     */
    public String getGenre() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TCO");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TCON");
        }

        int posParentheseFermante = 0;

        if (temp != null && !temp.equals("") && temp.substring(0, 1).equals("(")) {

            int longueur = temp.length();

            for (int i = 0; i < longueur; i++) {
                if (temp.substring(i, i + 1).equals(")")) {
                    posParentheseFermante = i;
                    break;
                }
            }

            if (posParentheseFermante != 0) {
                if (posParentheseFermante != longueur - 1) {
                    temp = temp.substring(posParentheseFermante + 1, longueur);
                } else {
                    return ID3v1Tag.TAB_STYLES_MUSIQUES_ID3V1[new Integer(temp.substring(1, longueur - 1)).intValue()];
                }
            }
        }
        return temp;
    }

    /**
     * Modifie le champ style dans l'objet.<BR><BR> <U>ATTENTION :</U> Pour que
     * la modification soit prise en compte dans le fichier m�me, il faut
     * appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param style La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setGenre(String style) throws Id3TagException {
        if (this.version == 2) {

            String temp = null;

            for (int i = 0; i < ID3v1Tag.TAB_STYLES_MUSIQUES_ID3V1.length; i++) {
                if (ID3v1Tag.TAB_STYLES_MUSIQUES_ID3V1[i].equals(style)) {
                    temp = "(" + i + ")";
                    break;
                }
            }

            if (temp != null) {
                this.setTextFrame("TCO", temp);
            } else {
                this.setTextFrame("TCO", style);
            }
        } else {
            this.setTextFrame("TCON", style);
        }
    }

    /**
     * @return L'URL contenu dans le tag id3v2 du fichier.
     */
    public String getURL(int indice) {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("WXX", indice);
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("WXXX", indice);
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ url dans l'objet.<BR><BR> <U>ATTENTION :</U> Pour que la
     * modification soit prise en compte dans le fichier m�me, il faut appeler
     * la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param url La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setURL(String url) throws Id3TagException {
        // Un tag existe...
        if (this.existTag) {
            // Ce tag est en version 2...
            if (this.version == 2) {
                // On n'a pas saisi d'artiste...                
                if (url.length() == 0) {
                    this.frames.removeFrame("WXX");
                } // On a saisi un artiste...
                else {
                    // On a saisi un artiste diff�rent de celui d�j� en m�moire...
                    if (this.frames.existFrame("WXX")) {
                        // La frame existait d�j�...on la met � jour
                        if (!frames.getFrameContent("WXX").equals(url)) {
                            this.frames.setFrameContent("WXX", url);
                            this.existModification = true;
                        }
                    } // La frame n'existait pas...on la cr�e.
                    else {
                        // On encode par d�faut en "ISO-8859-1", il faut donc ajouter un � la taille
                        // pour tenir compte du s�parateur.
                        this.frames.addURLFrameV2("WXX", new Integer(7 + url.length() + 1),
                                url, null, new Byte(this.BYTE_VALEUR_ZERO));
                    }
                }
            } // On est en version 3...
            else if (this.version == 3 || this.version == 4) {
                // On n'a pas saisi d'artiste...                
                if (url.length() == 0) {
                    this.frames.removeFrame("WXXX");
                } // On a saisi un artiste...
                else {
                    // On a saisi un artiste diff�rent de celui d�j� en m�moire...
                    if (this.frames.existFrame("WXXX")) {
                        // La frame existait d�j�...on la met � jour
                        if (!frames.getFrameContent("WXXX").equals(url)) {
                            this.frames.setFrameContent("WXXX", url);
                            this.existModification = true;
                        }
                    } // La frame n'existait pas...on la cr�e.
                    else {
                        // On encode par d�faut en "ISO-8859-1", il faut donc ajouter un � la taille
                        // pour tenir compte du s�parateur.
                        this.frames.addURLFrameV3("WXXX", new Integer(11 + url.length() + 1),
                                new Byte[]{this.BYTE_VALEUR_ZERO, this.BYTE_VALEUR_ZERO},
                                url, null, new Byte(this.BYTE_VALEUR_ZERO));
                    }
                }
            }
        } // Si aucun tag n'existe, on prend toujours la version 3 des tags ID3v2 pour en cr�er un nouveau.
        else {
            if (url.length() > 0) {
                // On encode par d�faut en "ISO-8859-1".
                // On ajoute 1 � la taille de la frame � cause de la description qui fait un caract�re ("\u0000").
                this.frames.addURLFrameV3("WXXX", new Integer(11 + url.length() + 1),
                        new Byte[]{this.BYTE_VALEUR_ZERO, this.BYTE_VALEUR_ZERO},
                        url, null, new Byte(this.BYTE_VALEUR_ZERO));
                this.existModification = true;
                this.version = 3;
            }
        }
    }

    /**
     * @return Le contenu du champ "encod� par" contenu dans le tag id3v2 du
     * fichier.
     */
    public String getEncodePar() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TEN");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TENC");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ "encodePar" dans l'objet.<BR><BR> <U>ATTENTION :</U>
     * Pour que la modification soit prise en compte dans le fichier m�me, il
     * faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param encodePar La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setEncodePar(String encodePar) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TEN", encodePar);
        } else {
            this.setTextFrame("TENC", encodePar);
        }
    }

    /**
     * @return Le copyright contenu dans le tag id3v2 du fichier.
     */
    public String getCopyright() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TCR");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TCOP");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ "copyright" dans l'objet.<BR><BR> <U>ATTENTION :</U>
     * Pour que la modification soit prise en compte dans le fichier m�me, il
     * faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param copyright La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setCopyright(String copyright) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TCR", copyright);
        } else {
            this.setTextFrame("TCOP", copyright);
        }
    }

    /**
     * @return L'artiste original contenu dans le tag id3v2 du fichier.
     */
    public String getArtisteOriginal() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TOA");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TOPE");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ "artisteOriginal" dans l'objet.<BR><BR> <U>ATTENTION
     * :</U> Pour que la modification soit prise en compte dans le fichier m�me,
     * il faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param artisteOriginal La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setArtisteOriginal(String artisteOriginal) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TOA", artisteOriginal);
        } else {
            this.setTextFrame("TOPE", artisteOriginal);
        }
    }

    /**
     * @return Le compositeur contenu dans le tag id3v2 du fichier.
     */
    public String getCompositeur() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TCM");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TCOM");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ "compositeur" dans l'objet.<BR><BR> <U>ATTENTION :</U>
     * Pour que la modification soit prise en compte dans le fichier m�me, il
     * faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param compositeur La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setCompositeur(String compositeur) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TCM", compositeur);
        } else {
            this.setTextFrame("TCOM", compositeur);
        }
    }

    /**
     * @return L'orchestre contenu dans l'objet.
     */
    public String getOrchestre() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TP2");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TPE2");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ "orchestre" dans l'objet.<BR><BR> <U>ATTENTION :</U>
     * Pour que la modification soit prise en compte dans le fichier m�me, il
     * faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param orchestre La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setOrchestre(String orchestre) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TP2", orchestre);
        } else {
            this.setTextFrame("TPE2", orchestre);
        }
    }

    /**
     * @return Le num�ro de disque dans l'objet (Peut �tre diff�rent de celui
     * dans le fichier).
     */
    public String getNumDisque() {

        String temp = "";

        if (this.version == 2) {
            temp = frames.getFrameContent("TPA");
        } else if (this.version == 3 || this.version == 4) {
            temp = frames.getFrameContent("TPOS");
        }

        if (temp == null) {
            temp = "";
        }

        return temp;
    }

    /**
     * Modifie le champ "numDisque" dans l'objet.<BR><BR> <U>ATTENTION :</U>
     * Pour que la modification soit prise en compte dans le fichier m�me, il
     * faut appeler la m�thode d'enregistrement "recordTag()".<BR><BR>
     *
     * @param numDisque La chaine de caract�res � enregistrer.
     * @throws <B>ID3Exception</B> Le type d'exception lev� en cas d'erreur.
     */
    public void setNumDisque(String numDisque) throws Id3TagException {
        if (this.version == 2) {
            this.setTextFrame("TPA", numDisque);
        } else {
            this.setTextFrame("TPOS", numDisque);
        }
    }

    /**
     * @return Le num�ro de r�vision du tag id3v2.
     */
    public int getRevision() {
        return this.revision;
    }

    /**
     * @return Le num�ro de version du tag id3v2.
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * @return "true" si le flag "indicateur exp�rimental" est positionn�,
     * "false" sinon.
     */
    public boolean isExpIndic() {
        return expIndic;
    }

    /**
     * @return "true" si le flag "header �tendu" est positionn�, "false" sinon.
     */
    public boolean isExtHeader() {
        return extHeader;
    }

    /**
     * @return "true" si le flag "footer" est positionn�, "false" sinon.
     */
    public boolean isFooter() {
        return footer;
    }

    /**
     * @return "true" si le flag "non synchronisation" est positionn�, "false"
     * sinon.
     */
    public boolean isUnsynchro() {
        return unsynchro;
    }

    /**
     * @return La taille du tag.
     */
    public int getTagSize() {
        return this.tagSize;
    }

    /**
     * @return Le nombre d'octets du padding.
     */
    public int getPadding() {
        return padding;
    }

    /**
     * @return TRUE si un tag existe, FALSE sinon.
     */
    public boolean existTag() {
        return this.existTag;
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
                this.modeLectureSeule = true;
            } catch (IOException e) {
                throw new Id3TagException(e, "Impossible de fermer l'objet d'acces eu fichier !!");
            }
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
                if (this.tagDesactive || this.frames.size() == 0) {
                    if (this.existTag) {
                        this.fichier.seek(0);
                        this.fichier.write(this.contenuSansTagId3v2);
                        this.fichier.setLength(this.fileLength - this.tagSize);
                        this.existTag = false;
                        this.tagSize = 0;
                        this.version = 0;
                        this.revision = 0;
                        this.padding = 0;
                        this.frames.clear();
                    } else {
                        return false;
                    }
                } else {
                    int sommeTaille = 0;
                    int realOffset = 0;

                    boolean haveToRewriteFile = false;

                    // Il faut regarder si la nouvelle somme des tailles des frames est sup�rieure � la
                    // taille totale du tag. Si ce n'est pas le cas, gr�ce au padding, on n'aura pas
                    // besoin de r��crire tout le fichier.
                    // On fait la somme des tailles de toutes les frames qu'on sait traiter.
                    for (int i = 0; i < this.frames.size(); i++) {
                        if (this.frames.isFrameIgnored(i)) {
                            sommeTaille += this.frames.getFrameIgnored(i).length;
                        } else {
                            Object[] retour = this.frames.getFrame(i);

                            if (retour != null && retour[1].toString() != null) {
                                sommeTaille += new Integer(retour[1].toString()).intValue();
                            }
                        }
                    }

                    if (sommeTaille <= this.tagSize) {
                        // La taille qu'on va enregistrer ne doit pas tenir des 10 octets du header.
                        sommeTaille = this.tagSize - 10;
                    } else {// On veut se redonner un padding d'un ko
                        sommeTaille += 1024;
                        // Il faut ajouter � la taille totale du tag les 10 octets du header.
                        this.tagSize = sommeTaille + 10;
                        haveToRewriteFile = true;
                    }

                    // On d�passe la capacit� max du tag...
                    if (sommeTaille > 268435455) {
                        throw new Id3TagException("Taille du tag trop grande");
                    }

                    byte[] taille = new byte[4];

                    taille[3] = (byte) (0x7F & sommeTaille);
                    taille[2] = (byte) (0x7F & (sommeTaille >> 7));
                    taille[1] = (byte) (0x7F & (sommeTaille >> 14));
                    taille[0] = (byte) (0x7F & (sommeTaille >> 21));

                    this.fichier.seek(0);
                    this.fichier.writeBytes("ID3");
                    this.fichier.write(this.version);
                    this.fichier.write(0);
                    this.fichier.write(0);
                    this.fichier.write(taille);

                    realOffset = 10;

                    for (int i = 0; i < frames.size(); i++) {
                        // Si ce n'est pas une frame ignor�e...
                        if (!this.frames.isFrameIgnored(i)) {

                            Object[] retour = frames.getFrame(i);

                            if (retour != null) {

                                int typeFrame = frames.getFrameType(retour[0].toString());

                                if (typeFrame == -1) {
                                    throw new Id3TagException("La frame est exp�rimentale");
                                }

                                if (typeFrame == -2) {
                                    throw new Id3TagException("Type de frame non pris en charge : " + retour[0].toString());
                                }

                                // Identifiant
                                this.fichier.seek(realOffset);
                                this.fichier.writeBytes(retour[0].toString());

                                // Taille de la frame	
                                int tailleFrame = new Integer(retour[1].toString()).intValue();

                                byte[] tailleFrameByte = null;

                                // La taille de la frame contenu dans l'objet tient compte du header
                                if (this.version == 2) {
                                    tailleFrameByte = new byte[3];
                                    tailleFrameByte[2] = (byte) (0xFF & (tailleFrame - 6));
                                    tailleFrameByte[1] = (byte) (0xFF & ((tailleFrame - 6) >> 8));
                                    tailleFrameByte[0] = (byte) (0xFF & ((tailleFrame - 6) >> 16));
                                } else if (this.version == 4) {
                                    tailleFrameByte = new byte[4];
                                    tailleFrameByte[3] = (byte) (0x7F & (tailleFrame - 10));
                                    tailleFrameByte[2] = (byte) (0x7F & ((tailleFrame - 10) >> 7));
                                    tailleFrameByte[1] = (byte) (0x7F & ((tailleFrame - 10) >> 14));
                                    tailleFrameByte[0] = (byte) (0x7F & ((tailleFrame - 10) >> 21));
                                } else {
                                    tailleFrameByte = new byte[4];
                                    tailleFrameByte[3] = (byte) (0xFF & (tailleFrame - 10));
                                    tailleFrameByte[2] = (byte) (0xFF & ((tailleFrame - 10) >> 8));
                                    tailleFrameByte[1] = (byte) (0xFF & ((tailleFrame - 10) >> 16));
                                    tailleFrameByte[0] = (byte) (0xFF & ((tailleFrame - 10) >> 24));
                                }

                                this.fichier.write(tailleFrameByte);

                                if (this.version == 3 || this.version == 4) {
                                    // flags
                                    Byte[] flags = (Byte[]) retour[2];

                                    this.fichier.write(flags[0].byteValue());
                                    this.fichier.write(flags[1].byteValue());
                                }

                                // Encodage
                                this.fichier.write(new Byte(retour[5].toString()).byteValue());

                                // $00 ISO-8859-1 [ISO-8859-1]. Terminated with $00.
                                // $01 UTF-16 [UTF-16] encoded Unicode [UNICODE] with BOM. 
                                //     All strings in the same frame SHALL have the same byteorder. 
                                //     Terminated with $00 00.
                                // $02 UTF-16BE [UTF-16] encoded Unicode [UNICODE] without BOM. 
                                //     Terminated with $00 00.
                                // $03 UTF-8 [UTF-8] encoded Unicode [UNICODE]. Terminated with $00.
                                String encoding = "";
                                String separateur = "";

                                switch (new Byte(retour[5].toString()).byteValue()) {
                                    case 0x00:
                                        encoding = "ISO-8859-1";
                                        separateur = "\u0000";
                                        break;
                                    case 0x01:
                                        encoding = "UTF-16";
                                        separateur = "\u0000\u0000";
                                        break;
                                    case 0x02:
                                        encoding = "UTF-16";
                                        separateur = "\u0000\u0000";
                                        break;
                                    case 0x03:
                                        encoding = "UTF-8";
                                        separateur = "\u0000";
                                        break;
                                    default:
                                        throw new Id3TagException("Encodage non pris en charge");
                                }

                                switch (typeFrame) {
                                    case ID3v2Frame.TEXT_INFORMATION_FRAME: {
                                        break;
                                    }
                                    case ID3v2Frame.COMMENT_FRAME: {
                                        // Langage
                                        this.fichier.writeBytes(retour[6].toString());
                                        // Il y a une description...
                                        if (retour[4] != null && retour[4].toString() != null
                                                && retour[4].toString() != "") {
                                            this.fichier.write(retour[4].toString().getBytes(encoding));
                                        }

                                        this.fichier.writeBytes(separateur);
                                        break;
                                    }
                                    case ID3v2Frame.URL_LINK_FRAME_WXXX: {
                                        // Il y a une description...
                                        if (retour[4] != null && retour[4].toString() != null
                                                && retour[4].toString() != "") {
                                            this.fichier.writeChars(retour[4].toString());
                                        }

                                        this.fichier.writeBytes(separateur);
                                        break;
                                    }
                                    default:
                                        // TODO Prendre en charge tous les types de frame.
                                        throw new Id3TagException("Type de frame non pris en charge : " + typeFrame);
                                }

                                // Contenu
                                this.fichier.write(retour[3].toString().getBytes(encoding));
                                realOffset += tailleFrame;
                            }
                        }
                    }

                    // Traitement des frames ignor�es (car non trait�es par l'objet actuellement)
                    for (int i = 0; i < this.frames.size(); i++) {
                        if (this.frames.isFrameIgnored(i)) {
                            this.fichier.write(this.frames.getFrameIgnored(i));
                            realOffset += this.frames.getFrameIgnored(i).length;
                        }
                    }

                    byte[] padding = new byte[this.tagSize - realOffset];

                    for (int i = 0; i < (this.tagSize - realOffset); i++) {
                        padding[i] = 0;
                    }

                    fichier.seek(realOffset);
                    fichier.write(padding);

                    if (haveToRewriteFile) {
                        this.fichier.seek(this.tagSize);
                        this.fichier.write(this.contenuSansTagId3v2);
                    }

                    this.padding = padding.length;
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
     * Mise � jour de l'indicateur indiquant si le tag va �tre ajout� ou
     * supprim� au prochain appel de la m�thode "recordTag()".
     *
     * @param tagDesactive TRUE pour supprimer le tag, FALSE pour l'ajouter au
     * prochain appel de la m�thode "recordTag()".
     */
    public void setTagDesactive(boolean tagDesactive) {
        this.tagDesactive = tagDesactive;
        this.existModification = true;
    }

    /**
     * M�thode g�n�rique d'enregistrement des frames de type texte.
     *
     * @param frameId L'identifiant de la frame.
     * @param content Le contenu de la frame.
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    private void setTextFrame(String frameId, String content) throws Id3TagException {
        // Un tag existe...
        if (this.existTag) {
            // Ce tag est en version 2...
            if (this.version == 2) {
                // On n'a pas saisi de donn�e (ou on a effac� l'ancien contenu)...                
                if (content.length() == 0) {
                    this.frames.removeFrame(frameId);
                } // On a saisi une donn�e...
                else {
                    // La frame existait d�j�...on la met � jour
                    if (this.frames.existFrame(frameId)) {
                        // On a saisi une donn�e diff�rente de celle d�j� en m�moire...
                        if (!frames.getFrameContent(frameId).equals(content)) {
                            this.frames.setFrameContent(frameId, content);
                            this.existModification = true;
                        }
                    } // La frame n'existait pas...on la cr�e.
                    else {
                        // On encode par d�faut en "UTF-16".
                        try {
                            this.frames.addTextFrameV2(frameId, new Integer(7 + content.getBytes("UTF-16").length),
                                    content, new Byte(this.BYTE_VALEUR_UN));
                        } catch (UnsupportedEncodingException e) {
                            throw new Id3TagException(e, "Impossible d'ajouter une frame (Probl�me d'encodage)");
                        }
                    }
                }
            } // On est en version 3...
            else if (this.version == 3 || this.version == 4) {
                // On n'a pas saisi de donn�e (ou on a effac� l'ancien contenu)...     
                if (content.length() == 0) {
                    this.frames.removeFrame(frameId);
                } // On a saisi une donn�e...
                else {
                    // La frame existait d�j�...on la met � jour
                    if (this.frames.existFrame(frameId)) {
                        // On a saisi une donn�e diff�rente de celle d�j� en m�moire...
                        if (!frames.getFrameContent(frameId).equals(content)) {
                            this.frames.setFrameContent(frameId, content);
                            this.existModification = true;
                        }
                    } // La frame n'existait pas...on la cr�e.
                    else {
                        // On encode par d�faut en "UTF-16".
                        try {
                            this.frames.addTextFrameV3(frameId, new Integer(11 + content.getBytes("UTF-16").length),
                                    new Byte[]{this.BYTE_VALEUR_ZERO, this.BYTE_VALEUR_ZERO},
                                    content, new Byte(this.BYTE_VALEUR_UN));
                        } catch (UnsupportedEncodingException e) {
                            throw new Id3TagException(e, "Impossible d'ajouter une frame (Probl�me d'encodage)");
                        }
                    }
                }
            }
        } // Si aucun tag n'existe, on prend toujours la version 3 des tags ID3v2 pour en cr�er un nouveau.
        else {
            if (content.length() > 0) {
                // On encode par d�faut en "UTF-16".
                try {
                    this.frames.addTextFrameV3(frameId, new Integer(11 + content.getBytes("UTF-16").length),
                            new Byte[]{this.BYTE_VALEUR_ZERO, this.BYTE_VALEUR_ZERO},
                            content, new Byte(this.BYTE_VALEUR_UN));
                } catch (UnsupportedEncodingException e) {
                    throw new Id3TagException(e, "Impossible d'ajouter une frame (Probl�me d'encodage)");
                }
                this.existModification = true;
                this.version = 3;
            }
        }
    }

    public int getNbChampsCommentaires() throws Id3TagException {

        Object[][] donnees = null;

        if (this.version == 2) {
            donnees = frames.getFrames("COM");
        } else if (this.version == 3 || this.version == 4) {
            donnees = frames.getFrames("COMM");
        }

        if (donnees == null) {
            return 0;
        }

        return donnees.length;
    }

    public String[][] getDatasChampsCommentaire() throws Id3TagException {

        String[][] retour = null;

        Object[][] donnees = null;

        if (this.getNbChampsCommentaires() <= 0) {
            return null;
        }

        retour = new String[this.getNbChampsCommentaires()][3];

        if (this.version == 2) {
            donnees = frames.getFrames("COM");
        } else if (this.version == 3 || this.version == 4) {
            donnees = frames.getFrames("COMM");
        }

        if (donnees != null) {
            for (int i = 0; i < donnees.length; i++) {
                if (donnees[i][6] == null) {
                    retour[i][0] = null;
                } else {
                    retour[i][0] = donnees[i][6].toString();
                }
                if (donnees[i][4] == null) {
                    retour[i][1] = null;
                } else {
                    retour[i][1] = donnees[i][4].toString();
                }
                if (donnees[i][3] == null) {
                    retour[i][2] = null;
                } else {
                    retour[i][2] = donnees[i][3].toString();
                }
            }
        }

        return retour;
    }

    public void garderUnSeulChampCommentaire(int indice) throws Id3TagException {

        // Quand on supprime une frame, la suivante prend l'occurence 0.
        // Il ne faut incr�menter le compteur que quand on veut garder la frame.
        int occurenceExistante = 0;
        int nbChampsCommentaire = this.getNbChampsCommentaires();

        for (int i = 0; i < nbChampsCommentaire; i++) {
            if (i != indice) {
                if (this.version == 2) {
                    this.frames.removeFrame("COM", occurenceExistante);
                } else if (this.version == 3 || this.version == 4) {
                    this.frames.removeFrame("COMM", occurenceExistante);
                }
            } else {
                occurenceExistante++;
            }
        }
    }
}