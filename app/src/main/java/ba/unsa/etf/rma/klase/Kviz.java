package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Kviz implements Serializable, Parcelable {

    private String naziv;
    private ArrayList<Pitanje> pitanja;
    private Kategorija kategorija;

    public Kviz() {
    }

    public Kviz(String naziv, ArrayList<Pitanje> pitanja, Kategorija kategorija) {
        this.naziv = naziv;
        this.pitanja = pitanja;
        this.kategorija = kategorija;
    }

    public static final Creator<Kviz> CREATOR = new Creator<Kviz>() {
        @Override
        public Kviz createFromParcel(Parcel parcel) {
            return new Kviz(parcel);
        }

        @Override
        public Kviz[] newArray(int i) {
            return new Kviz[i];
        }
    };

    protected Kviz(Parcel parcel) {
        naziv = parcel.readString();
        pitanja = parcel.readArrayList(Pitanje.class.getClassLoader());
        kategorija = parcel.readParcelable(Kategorija.class.getClassLoader());
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public ArrayList<Pitanje> getPitanja() {
        return pitanja;
    }

    public void setPitanja(ArrayList<Pitanje> pitanja) {
        this.pitanja = pitanja;
    }

    public Kategorija getKategorija() {
        return kategorija;
    }

    public void setKategorija(Kategorija kategorija) {
        this.kategorija = kategorija;
    }

    public void dodajPitanje(Pitanje pitanje) {
        pitanja.add(pitanje);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(naziv);
        parcel.writeList(pitanja);
        parcel.writeParcelable(kategorija, 0);
    }
}
