package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Kategorija implements Serializable, Parcelable {

    private String naziv;
    private String id;

    public Kategorija() {
    }

    public Kategorija(String naziv, String id) {
        this.naziv = naziv;
        this.id = id;
    }

    protected Kategorija(Parcel parcel) {
        naziv = parcel.readString();
        id = parcel.readString();
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static final Creator<Kategorija> CREATOR = new Creator<Kategorija>() {
        @Override
        public Kategorija createFromParcel(Parcel parcel) {
            return new Kategorija(parcel);
        }

        @Override
        public Kategorija[] newArray(int i) {
            return new Kategorija[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(naziv);
        parcel.writeString(id);
    }

}
