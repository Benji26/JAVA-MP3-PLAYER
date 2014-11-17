package net.garcia.benjamin.djroux_roux.ID;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Objet permettant de stocker les frames des tags ID3v2 (versions 2 et 3).
 * Cette classe s'appuie sur l'objet HashMap.
 *
 * @author mail : carpes0708-cg7@yahoo.fr
 * @version 2.0
 */
public class ID3v2Frame extends HashMap<Object, Object[]> {

    public static final int TEXT_INFORMATION_FRAME = 1;
    public static final int COMMENT_FRAME = 2;
    public static final int URL_LINK_FRAME = 3;
    public static final int MUSIC_CD_IDENTIFIER_FRAME = 4;
    public static final int UNIQUE_FILE_IDENTIFIER_FRAME = 5;
    public static final int URL_LINK_FRAME_WXXX = 6;
    public static final int POPULARIMETER_FRAME = 7;
    public static final int PRIVATE_FRAME = 8;
    public static final int USER_DEFINED_TEXT_INFORMATION_FRAME = 9;
    public static final int PLAY_COUNTER_FRAME = 10;
    public static final int GENERAL_ENCAPSULATED_OBJECT_FRAME = 11;
    public static final int ATTACHED_PICTURE_FRAME = 12;
    public static final int UNSYNCHRONISED_LYRICS_FRAME = 13;
    private int compteur = 0;
    private ArrayList<Integer> indiceFrameAIgnorer = new ArrayList<Integer>();

    /**
     * Constructeur de la classe.
     */
    public ID3v2Frame() {
        super();
    }

    /**
     * Cette m�thode retourne le type de frame ayant pour identifiant la chaine
     * pass�e en param�tre.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @return Le type de frame (Voir les constantes de l'objet).
     */
    public int getFrameType(String frameId) {
        if (frameId.substring(0, 1).equals("X")
                || frameId.substring(0, 1).equals("Y")
                || frameId.substring(0, 1).equals("Z")) {
            return -1;
        } else if (frameId.substring(0, 1).equals("T")) {
            if (!frameId.equals("TXXX") && !frameId.equals("TXX")) {
                return TEXT_INFORMATION_FRAME;
            }
            return USER_DEFINED_TEXT_INFORMATION_FRAME;
        } else if (frameId.equals("COMM") || frameId.equals("COM")) {
            return ID3v2Frame.COMMENT_FRAME;
        } else if (frameId.equals("WOAR")) {
            return ID3v2Frame.URL_LINK_FRAME;
        } else if (frameId.equals("WCOM") || frameId.equals("WCM")) {
            return ID3v2Frame.URL_LINK_FRAME;
        } else if (frameId.equals("WXXX") || frameId.equals("WXX")) {
            return ID3v2Frame.URL_LINK_FRAME_WXXX;
        } else if (frameId.equals("MCDI") || frameId.equals("MCI")) {
            return ID3v2Frame.MUSIC_CD_IDENTIFIER_FRAME;
        } else if (frameId.equals("UFID") || frameId.equals("UFI")) {
            return ID3v2Frame.UNIQUE_FILE_IDENTIFIER_FRAME;
        } else if (frameId.equals("POPM") || frameId.equals("POP")) {
            return ID3v2Frame.POPULARIMETER_FRAME;
        } else if (frameId.equals("PRIV")) {
            return ID3v2Frame.PRIVATE_FRAME;
        } else if (frameId.equals("PCNT") || frameId.equals("CNT")) {
            return ID3v2Frame.PLAY_COUNTER_FRAME;
        } else if (frameId.equals("GEOB") || frameId.equals("GEO")) {
            return ID3v2Frame.GENERAL_ENCAPSULATED_OBJECT_FRAME;
        } else if (frameId.equals("APIC") || frameId.equals("PIC")) {
            return ID3v2Frame.ATTACHED_PICTURE_FRAME;
        } else if (frameId.equals("USLT") || frameId.equals("ULT")) {
            return ID3v2Frame.UNSYNCHRONISED_LYRICS_FRAME;
        } else {
            return -2;
        }
    }

    /**
     * M�thode g�n�rique d'ajout d'une frame en m�moire.<BR><BR> En fonction du
     * type de frame, certain �l�ment peuvent �tre � null :<BR> <DD><LI>En
     * version 2, il n'y a pas de flags => on met null <DD><LI>Frame de type
     * texte, les champs description et langage sont inutilis�s => on met null
     * <DD><LI>Frame WXXX ou WXX, il n'y a pas de partie langage => on met
     * null<BR><BR> ATTENTION : la taille de la frame <U>DOIT PRENDRE EN COMPTE
     * LE HEADER DE LA FRAME !!</U><BR><BR> Pour faciliter la t�che, il existe
     * des m�thodes publiques sp�cialis�es qui s'appuient sur cette m�thode. De
     * plus, si des champs sont saisis alors qu'ils sont inutiles, on les
     * ignore.<BR><BR>
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param size La taille de la frame (y compris le header de 10 caract�res
     * en version 3 ou de 6 caract�res en version 2)
     * @param flags Les flags du header pour la version 3 (On passe toujours
     * null en version 2)
     * @param content Le contenu de la frame. Si vaut null, cette m�thode est
     * sans effet.
     * @param description Le descriptif pour les champs du type URL et
     * commentaire
     * @param encoding Le type d'encodage (Principalement ISO-8859-1 et UTF-16)
     * @param langage Le langage pour les champs du type commentaire.
     */
    public void addFrame(String frameId, Integer size, Byte[] flags, String content, String description, Byte encoding, String langage) {

        if (content != null) {

            // Dans le premier �l�ment, on met l'identifiant de la frame (son nom)
            // Dans le second �l�ment, on met sa taille.
            // Dans le troisi�me �l�ment, on met les flags
            // Dans le quatri�me �l�ment, on met le contenu.
            // Dans le cinqui�me �l�ment, on met le descriptif (n'existe pas pour tous les types de champ).
            // Dans le sixi�me �l�ment, on met le type d'encodage.
            // Dans le septi�me �l�ment, on met le langage (n'existe pas pour tous les types de champ)
            Object[] frame = new Object[7];

            frame[0] = frameId;
            frame[1] = size;
            frame[2] = flags;
            frame[3] = content;
            frame[4] = description;
            frame[5] = encoding;
            frame[6] = langage;

            String cle = "frame" + compteur;

            this.put(cle, frame);
            compteur++;
        }
    }

    /**
     * M�thode sp�cialis�e pour l'ajout d'une frame de type texte pour les tags
     * version 2. Elle s'appuie sur la m�thode addFrame (). Voir celle-ci pour
     * plus d'information
     */
    public void addTextFrameV2(String frameId, Integer size, String content, Byte encoding) {
        this.addFrame(frameId, size, null, content, null, encoding, null);
    }

    /**
     * M�thode sp�cialis�e pour l'ajout d'une frame de type texte pour les tags
     * version 3. Elle s'appuie sur la m�thode addFrame (). Voir celle-ci pour
     * plus d'information
     */
    public void addTextFrameV3(String frameId, Integer size, Byte[] flags, String content, Byte encoding) {
        this.addFrame(frameId, size, flags, content, null, encoding, null);
    }

    /**
     * M�thode sp�cialis�e pour l'ajout d'une frame de type URL pour les tags
     * version 2. Elle s'appuie sur la m�thode addFrame (). Voir celle-ci pour
     * plus d'information
     */
    public void addURLFrameV2(String frameId, Integer size, String content, String description, Byte encoding) {
        this.addFrame(frameId, size, null, content, description, encoding, null);
    }

    /**
     * M�thode sp�cialis�e pour l'ajout d'une frame de type URL pour les tags
     * version 3. Elle s'appuie sur la m�thode addFrame (). Voir celle-ci pour
     * plus d'information
     */
    public void addURLFrameV3(String frameId, Integer size, Byte[] flags, String content, String description, Byte encoding) {
        this.addFrame(frameId, size, flags, content, description, encoding, null);
    }

    /**
     * M�thode sp�cialis�e pour l'ajout d'une frame de type commentaire pour les
     * tags version 2. Elle s'appuie sur la m�thode addFrame (). Voir celle-ci
     * pour plus d'information
     */
    public void addCommentFrameV2(String frameId, Integer size, String content, String description, Byte encoding, String langage) {
        this.addFrame(frameId, size, null, content, description, encoding, langage);
    }

    /**
     * M�thode sp�cialis�e pour l'ajout d'une frame de type commentaire pour les
     * tags version 3. Elle s'appuie sur la m�thode addFrame (). Voir celle-ci
     * pour plus d'information
     */
    public void addCommentFrameV3(String frameId, Integer size, Byte[] flags, String content, String description, Byte encoding, String langage) {
        this.addFrame(frameId, size, flags, content, description, encoding, langage);
    }

    /**
     * Cette m�thode retourne la frame d'indice "indice" dans l'objet.
     *
     * @param indice L'indice dans l'objet
     * @return La frame sous forme d'un tableau d'objet.
     */
    public Object[] getFrame(int indice) {
        String cle = "frame" + indice;
        return (Object[]) this.get(cle);
    }

    /**
     * Retourne la frame sous forme de tableau d'objets.<BR><BR> S'il n'existe
     * pas de frame avec l'identifiant et l'occurence pass�s en param�tre, cette
     * m�thode retourne NULL.<BR>
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param occ L'occurence de l'identifiant
     * @return La taille de la frame (y compris le header de 10 caract�res en
     * version 3 ou de 6 caract�res en version 2)
     */
    public Object[] getFrame(String frameId, int occ) {

        int occurence = 0;

        for (int i = 0; i < this.size(); i++) {

            String cle = "frame" + i;

            Object[] frame = (Object[]) this.get(cle);

            if (frame != null && frame[0].toString().equals(frameId)) {
                if (occurence == occ) {
                    return frame;
                }

                occurence++;
            }
        }
        return null;
    }

    /**
     * Cette retourne toutes les frames qui ont comme identifiant celui pass� en
     * param�tre.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @return Un tableau � deux dimemnsions contenant toutes les frames
     * r�cup�r�es.
     */
    public Object[][] getFrames(String frameId) {

        Object[][] retour = null;
        ArrayList<Object[]> temp = new ArrayList<Object[]>();

        for (int i = 0; i < this.size(); i++) {

            String cle = "frame" + i;

            Object[] frame = (Object[]) this.get(cle);

            if (frame != null && frame[0].toString().equals(frameId)) {
                temp.add(frame);
            }
        }

        if (temp.size() > 0) {

            retour = new Object[temp.size()][6];

            for (int i = 0; i < temp.size(); i++) {
                retour[i] = temp.get(i);
            }
        }

        return retour;
    }

    /**
     * Retourne la taille de la frame dont l'identifiant est pass� en param�tre
     * et dont c'est la premi�re occurence. S'il n'existe pas de frame avec
     * l'identifiant pass� en param�tre, cette m�thode retourne NULL.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @return La taille de la frame (y compris le header de 10 caract�res en
     * version 3 ou de 6 caract�res en version 2)
     */
    public int getFrameSize(String frameId) {
        return this.getFrameSize(frameId, 0);
    }

    /**
     * Retourne la taille de la frame.<BR><BR> S'il n'existe pas de frame avec
     * l'identifiant et l'occurence pass�s en param�tre, cette m�thode retourne
     * NULL.<BR>
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param occ L'occurence de l'identifiant
     * @return La taille de la frame (y compris le header de 10 caract�res en
     * version 3 ou de 6 caract�res en version 2)
     */
    public int getFrameSize(String frameId, int occ) {

        Object[] retour = this.getFrame(frameId, occ);

        if (retour != null) {
            return new Integer(retour[1].toString()).intValue();
        }

        return -1;
    }

    /**
     * Retourne le contenu de la frame dont l'identifiant est pass� en param�tre
     * et dont c'est la premi�re occurence. S'il n'existe pas de frame avec
     * l'identifiant pass� en param�tre, cette m�thode retourne NULL.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @return Le contenu de la frame
     */
    public String getFrameContent(String frameId) {
        return this.getFrameContent(frameId, 0);
    }

    /**
     * Retourne le contenu de la frame.<BR><BR> S'il n'existe pas de frame avec
     * l'identifiant et l'occurence pass�s en param�tre, cette m�thode retourne
     * NULL.<BR>
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param occ L'occurence de l'identifiant
     * @return La taille de la frame (y compris le header de 10 caract�res en
     * version 3 ou de 6 caract�res en version 2)
     */
    public String getFrameContent(String frameId, int occ) {

        Object[] retour = this.getFrame(frameId, occ);

        if (retour != null) {
            return retour[3].toString();
        }

        return null;
    }

    /**
     * Retourne la taille de la frame dont l'identifiant est pass� en param�tre
     * et dont c'est la premi�re occurence. S'il n'existe pas de frame avec
     * l'identifiant en param�tre, cette m�thode retourne NULL.
     *
     * @param type Lidentifiant de la frame (4 caract�res majuscule en version
     * 3, 3 caract�res majuscule en version 2)
     * @param indice L'occurence de l'identifiant
     * @return La taille de la frame (y compris le header de 10 caract�res en
     * version 3 ou de 6 caract�res en version 2)
     */
    public String getFrameDescription(String type) {
        return this.getFrameDescription(type, 0);
    }

    /**
     * Retourne la taille de la frame dont l'identifiant est pass� en param�tre
     * et dont l'occurence est indice (certaines frames peuvent �tre multiples).
     * S'il n'existe pas de frame avec l'identifiant et l'occurence pass�s en
     * param�tre, cette m�thode retourne NULL.
     *
     * @param type Lidentifiant de la frame (4 caract�res majuscule en version
     * 3, 3 caract�res majuscule en version 2)
     * @param occ L'occurence de l'identifiant
     * @return La taille de la frame (y compris le header de 10 caract�res en
     * version 3 ou de 6 caract�res en version 2)
     */
    public String getFrameDescription(String frameId, int occ) {

        Object[] retour = this.getFrame(frameId, occ);

        if (retour != null) {
            return retour[4].toString();
        }

        return null;
    }

    /**
     * Retourne la langue de la frame. Si on appelle cette m�thode pour une
     * frame qui ne poss�de pas de langue, on retourne alors NULL. S'il n'existe
     * pas de frame avec l'identifiant en param�tre, cette m�thode retourne
     * NULL.
     *
     * @param type Lidentifiant de la frame (4 caract�res majuscule en version
     * 3, 3 caract�res majuscule en version 2)
     * @param indice L'occurence de l'identifiant
     * @return La taille de la frame (y compris le header de 10 caract�res en
     * version 3 ou de 6 caract�res en version 2)
     */
    public String getFrameLanguage(String type) {
        return this.getFrameLanguage(type, 0);
    }

    /**
     * Retourne la langue de la frame. Si on appelle cette m�thode pour une
     * frame qui ne poss�de pas de langue, on retourne alors NULL. S'il n'existe
     * pas de frame avec l'identifiant et l'occurence pass�s en param�tre, cette
     * m�thode retourne NULL.
     *
     * @param type Lidentifiant de la frame (4 caract�res majuscule en version
     * 3, 3 caract�res majuscule en version 2)
     * @param occ L'occurence de l'identifiant
     * @return La taille de la frame (y compris le header de 10 caract�res en
     * version 3 ou de 6 caract�res en version 2)
     */
    public String getFrameLanguage(String frameId, int occ) {

        Object[] retour = this.getFrame(frameId, occ);

        if (retour != null) {
            return retour[6].toString();
        }

        return null;
    }

    /**
     * Cette m�thode retourne TRUE s'il existe une frame ayant pour identifiant
     * la chaine de caract�res pass�e en param�tre, FALSE sinon.<BR>
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @return TRUE s'il existe une frame ayant pour identifiant la chaine de
     * caract�res pass�e en param�tre, FALSE sinon.
     */
    public boolean existFrame(String frameId) {
        return this.existFrame(frameId, 0);
    }

    /**
     * Cette m�thode retourne TRUE s'il existe une frame ayant pour identifiant
     * la chaine de caract�res pass�e en param�tre et d'occurence celle pass�e
     * en param�tre, FALSE sinon.<BR>
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @returnTRUE s'il existe une frame pour les param�tres pass�s, FALSE
     * sinon.
     */
    public boolean existFrame(String frameId, int occ) {

        Object[] retour = this.getFrame(frameId, occ);

        if (retour != null) {
            return true;
        }

        return false;
    }

    /**
     * Cette m�thode met � jour le contenu, la description et la langue d'une
     * frame de type COMM et existante pour l'identifiant et l'occurence pass�s
     * en param�tres.
     *
     * Quand un des param�tres vaut NULL, c'est qu'on ne doit pas le modifier
     * (Exception faite de l'identifiant).
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param indice L'occurence de l'identifiant
     * @param content Le nouveau contenu de la frame
     * @param description La description du champ.
     * @param langue La langue du champ.
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setFrame(String frameId, String langue, String description, String content, int occ)
            throws Id3TagException {

        int occurence = 0;

        if (frameId == null) {
            throw new Id3TagException("L'identifiant ne peut pas �tre NULL !!");
        }

        if (langue == null && description == null && content == null) {
            throw new Id3TagException("Il n'y a pas de donn�es � modifier !!");
        }

        if (langue != null && langue.length() != 3) {
            throw new Id3TagException("La taille de la langue n'est pas correcte !!");
        }

        if (occ < 0) {
            throw new Id3TagException("L'occurence doit �tre sup�rieure ou �gale � 0 !!");
        }

        for (int i = 0; i < this.size(); i++) {

            String cle = "frame" + i;

            Object[] retour = (Object[]) this.get(cle);

            if (retour != null && retour[0].toString().equals(frameId)) {

                if (occurence == occ) {
                    // $00 ISO-8859-1 [ISO-8859-1]. Terminated with $00.
                    // $01 UTF-16 [UTF-16] encoded Unicode [UNICODE] with BOM. 
                    //     All strings in the same frame SHALL have the same byteorder. Terminated with $00 00.
                    // $02 UTF-16BE [UTF-16] encoded Unicode [UNICODE] without BOM. Terminated with $00 00.
                    // $03 UTF-8 [UTF-8] encoded Unicode [UNICODE]. Terminated with $00.
                    String encoding = "";

                    switch (new Byte(retour[5].toString()).byteValue()) {
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

                    int newSize = new Integer(retour[1].toString()).intValue();

                    try {
                        if (description != null) {
                            newSize += (description.getBytes(encoding).length
                                    - retour[4].toString().getBytes(encoding).length);
                        }

                        if (content != null) {
                            newSize += (content.getBytes(encoding).length
                                    - retour[3].toString().getBytes(encoding).length);
                        }
                    } catch (Exception e) {
                        throw new Id3TagException(e, "Erreur lors du calcul de la nouvelle taille de la frame");
                    }

                    // Dans le premier �l�ment, on met l'identifiant de la frame (son nom)
                    // Dans le second �l�ment, on met sa taille.
                    // Dans le troisi�me �l�ment, on met les flags
                    // Dans le quatri�me �l�ment, on met le contenu.
                    // Dans le cinqui�me �l�ment, on met le descriptif (n'existe pas pour tous les types de champ).
                    // Dans le sixi�me �l�ment, on met le type d'encodage.
                    // Dans le septi�me �l�ment, on met le langage (n'existe pas pour tous les types de champ)
                    Object[] frame = new Object[7];

                    frame[0] = retour[0];
                    frame[1] = new Integer(newSize);
                    frame[2] = retour[2];

                    if (content != null) {
                        frame[3] = content;
                    } else {
                        frame[3] = retour[3];
                    }

                    if (description != null) {
                        frame[4] = description;
                    } else {
                        frame[4] = retour[4];
                    }

                    frame[5] = retour[5];

                    if (langue != null) {
                        frame[6] = langue;
                    } else {
                        frame[6] = retour[6];
                    }

                    this.put(cle, frame);
                    break;
                }

                occurence++;
            }
        }
    }

    /**
     * Cette m�thode met � jour le contenu d'une frame existante pour
     * l'identifiant dont c'est la premi�re occurence.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param content Le nouveau contenu de la frame
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setFrameContent(String frameId, String content) throws Id3TagException {
        this.setFrameContent(frameId, content, 0);
    }

    /**
     * Cette m�thode met � jour le contenu d'une frame existante pour
     * l'identifiant et l'occurence pass�s en param�tres.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param content Le nouveau contenu de la frame
     * @param indice L'occurence de l'identifiant
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setFrameContent(String frameId, String content, int occ) throws Id3TagException {
        this.setFrame(frameId, null, null, content, occ);
    }

    /**
     * Cette m�thode met � jour la description d'une frame existante.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param description La nouvelle description de la frame
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setFrameDescription(String frameId, String description) throws Id3TagException {
        this.setFrameDescription(frameId, description, 0);
    }

    /**
     * Cette m�thode met � jour la description d'une frame existante.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param description La nouvelle description de la frame.
     * @param indice L'occurence de l'identifiant
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setFrameDescription(String frameId, String description, int occ) throws Id3TagException {
        this.setFrame(frameId, null, description, null, occ);
    }

    /**
     * Cette m�thode met � jour la langue d'une frame existante.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param langue La nouvelle langue de la frame
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setFramelanguage(String frameId, String langue) throws Id3TagException {
        this.setFramelanguage(frameId, langue, 0);
    }

    /**
     * Cette m�thode met � jour la langue d'une frame existante.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     * @param langue La nouvelle langue de la frame.
     * @param indice L'occurence de l'identifiant
     * @throws Id3TagException Le type d'exception lev� en cas d'erreur.
     */
    public void setFramelanguage(String frameId, String langue, int occ) throws Id3TagException {
        this.setFrame(frameId, langue, null, null, occ);
    }

    /**
     * Cette m�thode supprime la frame dont l'identifiant est pass� en
     * param�tre. <BR><BR> <B>NOTE :</B> S'il y a plusieurs frames de ce type,
     * cette m�thode ne supprime que la premi�re occurence.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     */
    public void removeFrame(String frameId) {
        this.removeFrame(frameId, 0);
    }

    /**
     * Cette m�thode supprime la frame ayant l'identifiant et l'occurence pass�s
     * en param�tre.
     *
     * @param frameId Lidentifiant de la frame (4 caract�res majuscule en
     * version 3, 3 caract�res majuscule en version 2)
     */
    public void removeFrame(String frameId, int indice) {
        int occurence = 0;

        for (int i = 0; i < this.size(); i++) {

            String cle = "frame" + i;

            Object[] retour = (Object[]) this.get(cle);

            if (retour != null && retour[0].toString().equals(frameId)) {
                if (occurence == indice) {
                    this.remove(cle);
                    return;
                }

                occurence++;
            }
        }
    }

    /**
     * Ajout d'un frame qui n'est pas interpr�t�e correctement.
     *
     * @param content La frame compl�te sous forme de tableau de bytes.
     */
    public void addIgnoredFrame(Byte[] content) {

        if (content != null) {

            String cle = "frame" + this.compteur;

            this.put(cle, content);
            this.indiceFrameAIgnorer.add(new Integer(this.compteur));
            this.compteur++;
        }
    }

    /**
     * Pour savoir si la frame est une frame dont on connait la structure ou
     * pas.
     *
     * @param indice L'indice de la frame dans l'objet.
     * @return TRUE si c'est une frame dont on ne connait pas la structure,
     * FALSE sinon.
     */
    public boolean isFrameIgnored(int indice) {

        if (this.indiceFrameAIgnorer.contains(new Integer(indice))) {
            return true;
        }

        return false;
    }

    /**
     * R�cup�re le contenu d'un frame ignor�e.
     *
     * @param indice L'indice de la frame ignor�e dans l'objet.
     * @return Un tableau de bytes repr�sentant la frame.
     */
    public byte[] getFrameIgnored(int indice) {
        if (this.isFrameIgnored(indice)) {

            Byte[] frameIgnoree = (Byte[]) this.get("frame" + indice);

            byte[] contenu = new byte[frameIgnoree.length];

            for (int i = 0; i < frameIgnoree.length; i++) {
                contenu[i] = frameIgnoree[i].byteValue();
            }

            return contenu;
        }

        return null;
    }

    /**
     * @return La taille de l'objet.
     */
    public int size() {
        return this.compteur;
    }
}