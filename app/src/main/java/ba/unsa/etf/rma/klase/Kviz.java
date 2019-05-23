package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Objects;

public class Kviz implements Parcelable {

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
        pitanja = parcel.readArrayList(getClass().getClassLoader());
        kategorija = parcel.readParcelable(getClass().getClassLoader());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Kviz kviz = (Kviz) o;
        return Objects.equals(naziv, kviz.naziv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naziv);
    }

    public ArrayList<String> dajImenaPitanja() {
        ArrayList<String> imena = new ArrayList<>();
        for(Pitanje p : pitanja) imena.add(p.getNaziv());
        return imena;
    }
}
