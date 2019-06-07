package ba.unsa.etf.rma.klase;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import ba.unsa.etf.rma.interfejsi.FirestoreStorable;

import static ba.unsa.etf.rma.converteri.JSONQuestionConverter.findID;
import static ba.unsa.etf.rma.converteri.JSONQuestionConverter.findIndex;
import static ba.unsa.etf.rma.converteri.JSONQuestionConverter.findName;
import static ba.unsa.etf.rma.converteri.JSONQuestionConverter.getAnswers;

public class Pitanje implements Parcelable, FirestoreStorable {

    private String naziv;
    private String tekstPitanja;
    private ArrayList<String> odgovori = new ArrayList<>();
    private String tacan;
    private String documentID;
    private int ID;

    public Pitanje() {
    }

    public Pitanje(String naziv, String tekstPitanja, ArrayList<String> odgovori, String tacan) {
        this.naziv = naziv;
        this.tekstPitanja = tekstPitanja;
        this.odgovori = odgovori;
        this.tacan = tacan;
    }

    protected Pitanje(Parcel parcel) {
        naziv = parcel.readString();
        tekstPitanja = parcel.readString();
        odgovori = parcel.createStringArrayList();
        tacan = parcel.readString();
        documentID = parcel.readString();
        ID = parcel.readInt();
    }

    public static final Creator<Pitanje> CREATOR = new Creator<Pitanje>() {
        @Override
        public Pitanje createFromParcel(Parcel parcel) {
            return new Pitanje(parcel);
        }

        @Override
        public Pitanje[] newArray(int i) {
            return new Pitanje[i];
        }
    };

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getTekstPitanja() {
        return tekstPitanja;
    }

    public void setTekstPitanja(String tekstPitanja) {
        this.tekstPitanja = tekstPitanja;
    }

    public ArrayList<String> getOdgovori() {
        return odgovori;
    }

    public void setOdgovori(ArrayList<String> odgovori) {
        this.odgovori = odgovori;
    }

    public String getTacan() {
        return tacan;
    }

    public void setTacan(String tacan) {
        this.tacan = tacan;
    }

    public ArrayList<String> dajRandomOdgovore() {
        ArrayList<String> randomOdgovori = new ArrayList<>(odgovori);
        Collections.shuffle(randomOdgovori);
        return randomOdgovori;
    }

    public void dodajOdgovor(String odgovor) {
        odgovori.add(odgovor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pitanje pitanje = (Pitanje) o;
        return Objects.equals(naziv, pitanje.naziv);
    }

    @Override
    public int hashCode() {
        return Objects.hash(naziv);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(naziv);
        parcel.writeString(tekstPitanja);
        parcel.writeStringList(odgovori);
        parcel.writeString(tacan);
        parcel.writeString(documentID);
        parcel.writeInt(ID);
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getJSONFormat() {
        String json = "";
        int index = getOdgovori().indexOf(getTacan());
        json += "{\"fields\": {\"naziv\": {\"stringValue\": \"" + getNaziv() + "\"}," +
                "\"odgovori\": {\"arrayValue\": {\"values\": [";
        for(int i = 0; i < getOdgovori().size(); i++) {
            json += "{\"stringValue\": \"" +  getOdgovori().get(i) + "\"}";
            if(i != getOdgovori().size() - 1) json += ",";
        }
        json += "]}}, \"indexTacnog\": {\"integerValue\": \"" + index + "\"}}}";
        return json;
    }

    public static Pitanje convertFromJSON(JSONObject json) {
        Pitanje pitanje = new Pitanje();
        try {
            JSONObject fields = json.getJSONObject("fields");
            pitanje.setDocumentID(findID(json.getString("name")));
            pitanje.setNaziv(findName(fields.getJSONObject("naziv")));
            pitanje.setTekstPitanja(pitanje.getNaziv());
            pitanje.setOdgovori(getAnswers(fields.getJSONObject("odgovori").getJSONObject("arrayValue").getJSONArray("values")));
            pitanje.setTacan(pitanje.getOdgovori().get(findIndex(fields.getJSONObject("indexTacnog"))));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return pitanje;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PitanjeEntry.COLUMN_NAME, naziv);
        contentValues.put(PitanjeEntry.COLUMN_INDEX, odgovori.indexOf(tacan));
        return contentValues;
    }

    public ContentValues setContentValuesForAnswer(String answer) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(OdgovorEntry.COLUMN_NAME, answer);
        contentValues.put(OdgovorEntry.COLUMN_QUESTION_ID, ID);
        return contentValues;
    }

    public static class PitanjeEntry {
        private PitanjeEntry() {}

        public static final String TABLE_NAME = "Pitanja";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "naziv";
        public static final String COLUMN_INDEX = "index_tacnog";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                COLUMN_INDEX + " INTEGER NOT NULL);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] PROJECTION = new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_INDEX};
    }

    public static class OdgovorEntry {
        private OdgovorEntry() {}

        public static final String TABLE_NAME = "Odgovori";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "naziv";
        public static final String COLUMN_QUESTION_ID = "id_pitanja";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT NOT NULL," +
                COLUMN_QUESTION_ID + " INTEGER NOT NULL," +
                "FOREIGN KEY (" + COLUMN_QUESTION_ID + ") REFERENCES " + PitanjeEntry.TABLE_NAME +
                "(" + PitanjeEntry.COLUMN_ID + ") ON DELETE CASCADE);";
        public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME + ";";

        public static final String[] PROJECTION = new String[]{COLUMN_NAME};
    }
}
