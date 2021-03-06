package com.example.eden62.GENSMobile.Database.CampagneDatabase;


import android.os.Parcel;
import android.os.Parcelable;

import com.example.eden62.GENSMobile.Database.DatabaseItem;

/**
 * Représente une ligne de la table Campagne <br>
 * Pour le typeTaxon:<br>&nbsp
 *  - 0 signifie Faune<br>&nbsp
 *  - 1 signifie Flore<br>&nbsp
 *  - 2 signifie Oiseaux<br>&nbsp
 *  - 3 signifie Amphibiens<br>
 */
public class Inventaire implements Parcelable,DatabaseItem{

    private long _id;
    private long ref_taxon;
    private int appVersion;
    private int nv_taxon;
    private long user;
    private String nomFr;
    private String nomLatin;
    private int typeTaxon;
    private double latitude;
    private double longitude;
    private String date;
    private String heure;
    private int nombre;
    private String type_obs;
    private int nbMale;
    private int nbFemale;
    private String presencePonte;
    private String activite;
    private String statut;
    private String nidif;
    private int indiceAbondance = -1;
    private String remarques;
    private int err;

    /**
     * @param ref_taxon
     * @param user
     * @param typeTaxon
     * @param latitude
     * @param longitude
     * @param date
     * @param nombre
     * @param type_obs
     * @param nbMale
     * @param nbFemale
     * @param presencePonte
     * @param activite
     * @param statut
     * @param nidif
     * @param indiceAbondance
     */
    public Inventaire(long _id,long ref_taxon, int appVersion, int nv_taxon, long user, String nomFr, String nomLatin, int typeTaxon, double latitude, double longitude, String date, String heure, int nombre, String type_obs, String remarques,int nbMale, int nbFemale, String presencePonte, String activite, String statut, String nidif, int indiceAbondance, int err) {
        this._id = _id;
        this.ref_taxon = ref_taxon;
        this.appVersion = appVersion;
        this.nv_taxon = nv_taxon;
        this.user = user;
        this.nomFr = nomFr;
        this.nomLatin = nomLatin;
        this.typeTaxon = typeTaxon;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.heure = heure;
        this.nombre = nombre;
        this.type_obs = type_obs;
        this.remarques = remarques;
        this.nbMale = nbMale;
        this.nbFemale = nbFemale;
        if(presencePonte == null) {
            presencePonte = "false";
        }
        this.presencePonte = presencePonte;
        if(activite == null) {
            activite = "";
        }
        this.activite = activite;
        if(statut == null) {
            statut = "";
        }
        this.statut = statut;
        if(nidif == null) {
            nidif = "";
        }
        this.nidif = nidif;
        this.indiceAbondance = indiceAbondance;
        this.err = err;
    }

    /**
     * Celui commun à tous les inventaires
     * @param ref_taxon
     * @param user
     * @param latitude
     * @param longitude
     * @param date
     * @param nombre
     * @param type_obs
     */
    public Inventaire(long ref_taxon, int appVersion, int nv_taxon, long user, String nomFr, String nomLatin, int typeTaxon, double latitude, double longitude, String date, String heure, int nombre, String type_obs, String remarques) {
        this.ref_taxon = ref_taxon;
        this.appVersion = appVersion;
        this.nv_taxon = nv_taxon;
        this.nomFr = nomFr;
        this.nomLatin = nomLatin;
        this.typeTaxon = typeTaxon;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.heure = heure;
        this.type_obs = type_obs;
        this.remarques = remarques;
        this.user = user;
        this.nombre = nombre;
        this.presencePonte = "false";
        this.activite = "";
        this.statut = "";
        this.nidif = "";
    }

    /**
     * Pour la faune
     * @param ref_taxon
     * @param user
     * @param latitude
     * @param longitude
     * @param date
     * @param nombre
     * @param type_obs
     * @param nbMale
     * @param nbFemale
     */
    public Inventaire(long ref_taxon, int appVersion, int nv_taxon, long user, String nomFr, String nomLatin, int typeTaxon, double latitude, double longitude, String date, String heure, int nombre, String type_obs, String remarques,int nbMale, int nbFemale) {
        this(ref_taxon, appVersion, nv_taxon, user, nomFr, nomLatin, typeTaxon, latitude, longitude, date, heure, nombre, type_obs, remarques);
        this.nbMale = nbMale;
        this.nbFemale = nbFemale;
    }

    /**
     * Pour les oiseaux
     * @param ref_taxon
     * @param user
     * @param typeTaxon
     * @param latitude
     * @param longitude
     * @param date
     * @param nombre
     * @param type_obs
     * @param nbMale
     * @param nbFemale
     * @param activite
     * @param statut
     * @param nidif
     */
    public Inventaire(long ref_taxon, int appVersion, int nv_taxon, long user, String nomFr, String nomLatin, int typeTaxon, double latitude, double longitude, String date, String heure, int nombre, String type_obs, String remarques,int nbMale, int nbFemale, String activite, String statut, String nidif) {
        this(ref_taxon, appVersion, nv_taxon, user, nomFr, nomLatin, typeTaxon, latitude, longitude, date, heure, nombre, type_obs, remarques,nbMale, nbFemale);
        this.activite = activite;
        this.statut = statut;
        this.nidif = nidif;
    }

    /**
     * Pour les amphibiens
     * @param ref_taxon
     * @param user
     * @param typeTaxon
     * @param latitude
     * @param longitude
     * @param date
     * @param nombre
     * @param type_obs
     * @param nbMale
     * @param nbFemale
     * @param presencePonte
     */
    public Inventaire(long ref_taxon, int appVersion, int nv_taxon, long user, String nomFr, String nomLatin,  int typeTaxon, double latitude, double longitude, String date, String heure, int nombre, String type_obs, String remarques, int nbMale, int nbFemale, String presencePonte) {
        this(ref_taxon, appVersion, nv_taxon, user, nomFr, nomLatin, typeTaxon, latitude, longitude, date, heure, nombre, type_obs,  remarques, nbMale, nbFemale);
        this.presencePonte = presencePonte;
    }

    /**
     * Pour la flore
     * @param ref_taxon
     * @param user
     * @param latitude
     * @param longitude
     * @param date
     * @param nombre
     * @param type_obs
     * @param indiceAbondance
     */
    public Inventaire(long ref_taxon, int appVersion, int nv_taxon, long user, String nomFr, String nomLatin,  int typeTaxon, double latitude, double longitude, String date, String heure, int nombre, String type_obs, String remarques, int indiceAbondance) {
        this(ref_taxon, appVersion, nv_taxon, user, nomFr, nomLatin, typeTaxon, latitude, longitude, date, heure, nombre, type_obs, remarques);
        nbMale = -1;
        nbFemale = -1;
        this.indiceAbondance = indiceAbondance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(_id);
        out.writeLong(ref_taxon);
        out.writeInt(appVersion);
        out.writeInt(nv_taxon);
        out.writeLong(user);
        out.writeString(nomFr);
        out.writeString(nomLatin);
        out.writeInt(typeTaxon);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(date);
        out.writeString(heure);
        out.writeInt(nombre);
        out.writeString(type_obs);
        out.writeString(remarques);
        out.writeInt(nbMale);
        out.writeInt(nbFemale);
        out.writeString(presencePonte);
        out.writeString(activite);
        out.writeString(statut);
        out.writeString(nidif);
        out.writeInt(indiceAbondance);
        out.writeInt(err);
    }

    public static final Parcelable.Creator<Inventaire> CREATOR = new Parcelable.Creator<Inventaire>() {
        public Inventaire createFromParcel(Parcel in) {
            return new Inventaire(in);
        }

        public Inventaire[] newArray(int size) {
            return new Inventaire[size];
        }
    };

    private Inventaire(Parcel in) {
        _id = in.readLong();
        ref_taxon= in.readLong();
        appVersion = in.readInt();
        nv_taxon = in.readInt();
        user= in.readLong();
        nomFr = in.readString();
        nomLatin = in.readString();
        typeTaxon = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        date = in.readString();
        heure = in.readString();
        nombre = in.readInt();
        type_obs = in.readString();
        remarques = in.readString();
        nbMale = in.readInt();
        nbFemale = in.readInt();
        presencePonte = in.readString();
        activite = in.readString();
        statut = in.readString();
        nidif = in.readString();
        indiceAbondance = in.readInt();
        err = in.readInt();
    }

    public int getNombre() {
        return nombre;
    }

    public void setNombre(int nombre) {
        this.nombre = nombre;
    }

    public String getType_obs() {
        return type_obs;
    }

    public void setType_obs(String type_obs) {
        this.type_obs = type_obs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getRef_taxon() {
        return ref_taxon;
    }

    public void setRef_taxon(long ref_taxon) {
        this.ref_taxon = ref_taxon;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getNbMale() {
        return nbMale;
    }

    public void setNbMale(int nbMale) {
        this.nbMale = nbMale;
    }

    public int getNbFemale() {
        return nbFemale;
    }

    public void setNbFemale(int nbFemale) {
        this.nbFemale = nbFemale;
    }

    public String getPresencePonte() {
        return presencePonte;
    }

    public void setPresencePonte(String presencePonte) {
        this.presencePonte = presencePonte;
    }

    public String getActivite() {
        return activite;
    }

    public void setActivite(String activite) {
        this.activite = activite;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public String getNidif() {
        return nidif;
    }

    public void setNidif(String nidif) {
        this.nidif = nidif;
    }

    public int getIndiceAbondance() {
        return indiceAbondance;
    }

    public void setIndiceAbondance(int indiceAbondance) {
        this.indiceAbondance = indiceAbondance;
    }

    public int getTypeTaxon() {
        return typeTaxon;
    }

    public void setTypeTaxon(int typeTaxon) {
        this.typeTaxon = typeTaxon;
    }

    public String getNomFr() {
        return nomFr;
    }

    public void setNomFr(String nomFr) {
        this.nomFr = nomFr;
    }

    public String getNomLatin() {
        return nomLatin;
    }

    public void setNomLatin(String nomLatin) {
        this.nomLatin = nomLatin;
    }

    public String getHeure() {
        return heure;
    }

    public void setHeure(String heure) {
        this.heure = heure;
    }

    public int getErr() {
        return err;
    }

    public boolean isToSync(){
        return err == 0;
    }

    public void setErr(int err) {
        this.err = err;
    }

    public int getNv_taxon() {
        return nv_taxon;
    }

    public void setNv_taxon(int nv_taxon) {
        this.nv_taxon = nv_taxon;
    }

    public String getRemarques() {
        return remarques;
    }

    public void setRemarques(String remarques) {
        this.remarques = remarques;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    @Override
    public int hashCode() {
        return (int)this._id;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Inventaire){
            Inventaire other = (Inventaire) o;
            return (this.hashCode() == other.hashCode()) && this.heure.equals(other.heure);
        }return false;
    }
}
