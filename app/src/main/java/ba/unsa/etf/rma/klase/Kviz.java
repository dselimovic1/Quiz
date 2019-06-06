package ba.unsa.etf.rma.klase;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import ba.unsa.etf.rma.interfejsi.FirestoreStorable;

import static ba.unsa.etf.rma.converteri.JSONQuizConverter.findID;
import static ba.unsa.etf.rma.converteri.JSONQuizConverter.findName;
import static ba.unsa.etf.rma.converteri.JSONQuizConverter.getCategory;
import static ba.unsa.etf.rma.converteri.JSONQuizConverter.getQuestions;

public class Kviz implements Parcelable, FirestoreStorable {

    private String naziv;
    private ArrayList<Pitanje> pitanja = new ArrayList<>();
    private Kategorija kategorija;
    private String documentID;
    private int ID;

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
        documentID = parcel.readString();
        ID = parcel.readInt();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(naziv);
        parcel.writeList(pitanja);
        parcel.writeParcelable(kategorija, 0);
        parcel.writeString(documentID);
        parcel.writeInt(ID);
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

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getJSONFormat() {
        String json = "";
        json += "{\"fields\": {\"pitanja\": {\"arrayValue\": {\"values\": [";
        for(int i = 0; i < pitanja.size(); i++) {
            json += "{\"stringValue\": \"" + pitanja.get(i).getDocumentID() + "\"}";
            if(i != pitanja.size() - 1) json += ",";
        }
        json += "]}},\"idKategorije\": {\"stringValue\": \"" + getKategorija().getDocumentID() + "\"}," +
                "\"naziv\": {\"stringValue\": \"" + getNaziv() + "\"}}}";
        return json;
    }

    public static Kviz convertFromJSON(JSONObject json) {
        Kviz kviz = new Kviz();
        try {
            JSONObject fields = json.getJSONObject("fields");
            kviz.setDocumentID(findID(json.getString("name")));
            kviz.setNaziv(findName(fields.getJSONObject("naziv")));
            if(fields.getJSONObject("pitanja").getJSONObject("arrayValue").has("values"))
                kviz.setPitanja(getQuestions(fields.getJSONObject("pitanja").getJSONObject("arrayValue").getJSONArray("values")));
            else
                kviz.setPitanja(new ArrayList<Pitanje>());
            kviz.setKategorija(getCategory(fields.getJSONObject("idKategorije")));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return kviz;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public static class KvizEntry {
        private KvizEntry() {}

        public static final String TABLE_NAME = "Kvizovi";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "naziv";
        public static final String COLUMN_CATEGORY_ID = "id_kategorije";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                COLUMN_CATEGORY_ID + " INTEGER," +
                "FOREIGN KEY (" + COLUMN_CATEGORY_ID + ") REFERENCES " + Kategorija.KategorijaEntry.TABLE_NAME +
                "(" + Kategorija.KategorijaEntry.COLUMN_ID + "));";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }

    public static class PitanjaKvizaEntry {
        private PitanjaKvizaEntry() {}

        public static final String TABLE_NAME = "PitanjaKviza";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_QUIZ_ID = "id_kviza";
        public static final String COLUMN_QUESTION_ID = "id_pitanja";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_QUESTION_ID + " INTEGER NOT NULL," +
                COLUMN_QUIZ_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + COLUMN_QUESTION_ID + ") REFERENCES " + Pitanje.PitanjeEntry.TABLE_NAME +
                "(" + Pitanje.PitanjeEntry.COLUMN_ID + ")," +
                "FOREIGN KEY (" + COLUMN_QUIZ_ID + ") REFERENCES " + KvizEntry.TABLE_NAME +
                "(" + KvizEntry.COLUMN_ID + "));";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";
    }
}
